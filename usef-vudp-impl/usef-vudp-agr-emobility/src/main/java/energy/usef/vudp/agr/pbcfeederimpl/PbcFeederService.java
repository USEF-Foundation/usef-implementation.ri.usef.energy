/*
 * Copyright 2015 USEF Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package energy.usef.vudp.agr.pbcfeederimpl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import javax.inject.Inject;

import energy.usef.core.util.PtuUtil;
import energy.usef.vudp.pbcfeeder.dto.Settings;
import energy.usef.vudp.pbcfeeder.dto.Connection;
import energy.usef.vudp.pbcfeeder.dto.Device;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import energy.usef.agr.dto.ConnectionPortfolioDto;
import energy.usef.agr.dto.ElementDto;
import energy.usef.agr.dto.ElementDtuDataDto;
import energy.usef.agr.dto.ElementTypeDto;

import energy.usef.vudp.pbcfeeder.PbcFeederClient;

/**
 * This class is the entry point for all data that is being 'fed' to the Aggregator PBCs.
 */
public class PbcFeederService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PbcFeederService.class);

    private static final String ELEMENT_SYNTHETIC_DATA_PROFILE_PREFIX = "UCL";
    private static final String ELEMENT_SYNTHETIC_DATA_SUFFIX = "UCL";
    private static final int MINUTES_PER_DAY = 24 * 60;
    private static final boolean SYNTHETIC_DATA = true;
    private static final boolean MANAGED_DEVICE = false;

    @Inject
    private PbcFeederClient pbcFeederClient;

    /**
     * Service which extracts data from the pbc feeder client and creates elements based on
     * this data, the connectionportfolio and the given period.
     *
     * @param connectionPortfolioDtoList current connection portfolio
     * @param period period for which elements should be created
     * @param ptuSize duriation of a PTU in minutes
     * @return List<ElementDto> a list of elements to be created for the given period
     */
    public List<ElementDto> fillElementsFromPBCFeeder(List<ConnectionPortfolioDto> connectionPortfolioDtoList, LocalDate period,
            Integer ptuSize) {

        List<ElementDto> elementDtoList = new ArrayList<>();

        //Determine number of ptus per day based on ptu size
        int ptusPerDay = PtuUtil.getNumberOfPtusPerDay(period, ptuSize);

        // fetch PBC data
        List<Connection> connections = pbcFeederClient.findConnections();
        LOGGER.debug("Nr of connections found in PBC feeder {}",connections.size());

        Map<String,Object> settings = pbcFeederClient.findSettings();
        LOGGER.debug("Following settings found {}",settings.toString());

        Integer pbcDay = determinePbcDay(period, settings);
        LOGGER.debug("Following PBC day determined {}",pbcDay);

        // go through connections and create synthetic and managed device elements
        connectionPortfolioDtoList.stream().forEach(connectionPortfolioDTO -> {
            Connection pbcFeederConnection = findConnectionInPbcData(connections, connectionPortfolioDTO);

            if(pbcFeederConnection != null){
                elementDtoList.add(createSyntheticDataElement(ptuSize, ptusPerDay, connectionPortfolioDTO, pbcFeederConnection, pbcDay));

                elementDtoList.addAll(createManagedDeviceElements(connectionPortfolioDTO, pbcFeederConnection, pbcDay));
            }
            else{
                LOGGER.error("Connection {} not found in PBC feeder Excelsheet",connectionPortfolioDTO.getConnectionEntityAddress());
            }
        });

        return elementDtoList;
    }

    private Integer determinePbcDay(LocalDate period, Map<String, Object> settings) {
        List<String> pbcDays = (List<String>) settings.get("dayCycle");
        Integer dayOfYear = period.getDayOfYear() - 1;
        Integer index = dayOfYear % pbcDays.size();
        return Integer.parseInt(pbcDays.get(index));
    }

    private Connection findConnectionInPbcData(List<Connection> connections, ConnectionPortfolioDto connectionPortfolioDTO) {
        Connection pbcFeederConnection = connections.stream()
                .filter(connection -> connection.getEan().equals(connectionPortfolioDTO.getConnectionEntityAddress()))
                .findAny()
                .orElse(null);
        return pbcFeederConnection;
    }

    private List<ElementDto> createManagedDeviceElements(ConnectionPortfolioDto connectionPortfolioDTO, Connection pbcFeederConnection, Integer pbcDay) {
        List<ElementDto> managedDeviceElementList = new ArrayList<>();
        pbcFeederConnection.getDevices().forEach(device -> {
            int dtuSize = device.getDtuSize();
            int dtusPerDay = MINUTES_PER_DAY / dtuSize;

            ElementDto managedDeviceElement = buildElement(device.getEndpoint(), dtuSize,
                    ElementTypeDto.MANAGED_DEVICE, device.getProfile(),
                    connectionPortfolioDTO.getConnectionEntityAddress(),MANAGED_DEVICE);

            addManagedDeviceElementDtuData(managedDeviceElement, device, pbcDay, dtusPerDay);

            managedDeviceElementList.add(managedDeviceElement);
        });

        return managedDeviceElementList;
    }

    private void addManagedDeviceElementDtuData(ElementDto managedDeviceElement, Device device, Integer pbcDay, Integer dtusPerDay) {
        BigDecimal forecastDeviation = BigDecimal.ONE.add(device.getForecastDeviation());
        for (int dtuIndex = 1; dtuIndex <= dtusPerDay; dtuIndex++) {
            ElementDtuDataDto elementDtuDataDto = new ElementDtuDataDto();
            elementDtuDataDto.setDtuIndex(dtuIndex);
            BigDecimal profileAverageConsumption = BigDecimal.valueOf(device.getPowerPerDayPerPtu().get(pbcDay).get(dtuIndex).getConsumption());
            BigDecimal profileAverageProduction = BigDecimal.valueOf(device.getPowerPerDayPerPtu().get(pbcDay).get(dtuIndex).getProduction());
            BigDecimal profilePotentialFlexConsumption = BigDecimal.valueOf(device.getPowerPerDayPerPtu().get(pbcDay).get(dtuIndex).getFlexConsumption());
            BigDecimal profilePotentialFlexProduction = BigDecimal.valueOf(device.getPowerPerDayPerPtu().get(pbcDay).get(dtuIndex).getFlexProduction());

            elementDtuDataDto.setProfileAverageConsumption(profileAverageConsumption.multiply(forecastDeviation).toBigInteger());
            elementDtuDataDto.setProfileAverageProduction(profileAverageProduction.multiply(forecastDeviation).toBigInteger());
            elementDtuDataDto.setProfilePotentialFlexConsumption(profilePotentialFlexConsumption.multiply(forecastDeviation).toBigInteger());
            elementDtuDataDto.setProfilePotentialFlexProduction(profilePotentialFlexProduction.multiply(forecastDeviation).toBigInteger());

            managedDeviceElement.getElementDtuData().add(elementDtuDataDto);
        }
    }

    private ElementDto createSyntheticDataElement(Integer ptuSize, Integer ptusPerDay, ConnectionPortfolioDto connectionPortfolioDTO, Connection pbcFeederConnection, Integer pbcDay) {
        ElementDto syntheticDataElement = buildElement(ELEMENT_SYNTHETIC_DATA_SUFFIX, ptuSize,
                ElementTypeDto.SYNTHETIC_DATA, ELEMENT_SYNTHETIC_DATA_PROFILE_PREFIX,
                connectionPortfolioDTO.getConnectionEntityAddress(), SYNTHETIC_DATA);
        for (int ptuIndex = 1; ptuIndex <= ptusPerDay; ptuIndex++) {
            ElementDtuDataDto elementDtuDataDto = new ElementDtuDataDto();
            elementDtuDataDto.setDtuIndex(ptuIndex);
            elementDtuDataDto.setProfileUncontrolledLoad(BigInteger.valueOf(pbcFeederConnection.getUncontrolled().get(pbcDay).get(ptuIndex).getForecast()));
            syntheticDataElement.getElementDtuData().add(elementDtuDataDto);
        }
        return syntheticDataElement;
    }

    private ElementDto buildElement(String id, Integer dtuSize, ElementTypeDto elementType, String profile,
                                    String connectionEntityAddress, boolean syntheticData) {
        ElementDto elementDto = new ElementDto();
        if(syntheticData){elementDto.setId(connectionEntityAddress + "." + id);}
        else{elementDto.setId(id);}
        elementDto.setElementType(elementType);
        elementDto.setProfile(profile);
        elementDto.setConnectionEntityAddress(connectionEntityAddress);
        elementDto.setDtuDuration(dtuSize);

        return elementDto;
    }
}
