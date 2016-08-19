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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;

import energy.usef.vudp.pbcfeeder.dto.Data;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import energy.usef.agr.dto.ConnectionPortfolioDto;
import energy.usef.agr.dto.ElementDto;
import energy.usef.agr.dto.ElementDtuDataDto;
import energy.usef.agr.dto.ElementTypeDto;
import energy.usef.agr.dto.ForecastPowerDataDto;
import energy.usef.agr.dto.PowerContainerDto;
import energy.usef.agr.dto.PowerDataDto;
import energy.usef.agr.dto.UdiPortfolioDto;
import energy.usef.core.util.DateTimeUtil;
import energy.usef.core.util.PtuUtil;

import energy.usef.vudp.pbcfeeder.PbcFeederClient;

/**
 * This class is the entry point for all data that is being 'fed' to the Aggregator PBCs.
 */
public class PbcFeederService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PbcFeederService.class);

    @Inject
    private PbcFeederClient pbcFeederClient;

    public List<ElementDto> fillElementsFromPBCFeeder(List<ConnectionPortfolioDto> connectionPortfolioDtoList, LocalDate period,
            Integer ptusPerDay, Integer ptuSize) {
        LOGGER.info("fillElementsFromPBCFeeder is invoked");
        List<ElementDto> elementDtoList = new ArrayList<>();
        Data data = pbcFeederClient.findData();

        /*
        // fetch PBC data for the period
        List<PbcStubDataDto> pbcStubDataList = pbcFeederClient.getPbcStubDataList(period, 1, ptusPerDay);

        // map the PBC data into uncontrolled load and PV load forecast
        Map<Integer, BigInteger> uncontrolledLoadPerPtu = fetchUncontrolledLoad(pbcStubDataList);
        //pv is production so should be negative for the methods below.
        Map<Integer, BigInteger> pvLoadForecastPerPtu = negateMap(fetchPVLoadForecast(pbcStubDataList));

        // for each connection create 3 elements (PV1 and ADS1 and UCL1)
        connectionPortfolioDtoList.stream().forEach(connectionPortfolioDTO -> {

            elementDtoList.add(createManagedDeviceForADS(period, ptusPerDay, ptuSize, connectionPortfolioDTO));

            elementDtoList.add(createManagedDeviceForPV(ptusPerDay, ptuSize, pvLoadForecastPerPtu, connectionPortfolioDTO));

            elementDtoList.add(createSyntheticData(ptusPerDay, ptuSize, uncontrolledLoadPerPtu, connectionPortfolioDTO));
        });
        */

        return elementDtoList;
    }

    /*
    private ElementDto createSyntheticData(Integer ptusPerDay, Integer ptuSize, Map<Integer, BigInteger> uncontrolledLoadPerPtu,
            ConnectionPortfolioDto connectionPortfolioDTO) {
        ElementDto syntheticDataElement = buildElement(ELEMENT_SYNTHETIC_DATA_SUFFIX, ptuSize,
                ElementTypeDto.SYNTHETIC_DATA, ELEMENT_SYNTHETIC_DATA_PROFILE_PREFIX,
                connectionPortfolioDTO.getConnectionEntityAddress());
        // add uncontrolled load to the synthetic data element (dtu size = ptu size)
        for (int dtuIndex = 1; dtuIndex <= ptusPerDay; dtuIndex++) {
            ElementDtuDataDto elementDtuDataDto = new ElementDtuDataDto();
            elementDtuDataDto.setDtuIndex(dtuIndex);
            elementDtuDataDto.setProfileUncontrolledLoad(uncontrolledLoadPerPtu.get(elementDtuDataDto.getDtuIndex()));
            syntheticDataElement.getElementDtuData().add(elementDtuDataDto);
        }
        return syntheticDataElement;
    }

    private ElementDto createManagedDeviceForPV(Integer ptusPerDay, Integer ptuSize, Map<Integer, BigInteger> pvLoadForecastPerPtu,
            ConnectionPortfolioDto connectionPortfolioDTO) {
        ElementDto managedDevicePVElement = buildElement(ELEMENT_MANAGED_DEVICE_PV_SUFFIX, ptuSize,
                ElementTypeDto.MANAGED_DEVICE, ELEMENT_PV_PROFILE_PREFIX, connectionPortfolioDTO.getConnectionEntityAddress());
        addElementData(ptusPerDay, pvLoadForecastPerPtu, managedDevicePVElement);
        return managedDevicePVElement;
    }

    private ElementDto createManagedDeviceForADS(LocalDate period, Integer ptusPerDay, Integer ptuSize,
            ConnectionPortfolioDto connectionPortfolioDTO) {
        Map<Integer, BigInteger> adsLoadPerPtu = createADSLoadMap(period, ptuSize);

        ElementDto managedDeviceADSElement = buildElement(ELEMENT_MANAGED_DEVICE_ADS_SUFFIX, ptuSize,
                ElementTypeDto.MANAGED_DEVICE, ELEMENT_ADS_PROFILE_PREFIX, connectionPortfolioDTO.getConnectionEntityAddress());
        addElementData(ptusPerDay, adsLoadPerPtu, managedDeviceADSElement);
        return managedDeviceADSElement;
    }

    private ElementDto buildElement(String id, Integer dtuSize, ElementTypeDto elementType, String profile,
            String connectionEntityAddress) {
        ElementDto elementDto = new ElementDto();
        elementDto.setId(connectionEntityAddress + "." + id);
        elementDto.setElementType(elementType);
        elementDto.setProfile(profile);
        elementDto.setConnectionEntityAddress(connectionEntityAddress);
        elementDto.setDtuDuration(dtuSize);

        return elementDto;
    }

    private void addElementData(Integer ptusPerDay, Map<Integer, BigInteger> loadPerDtu, ElementDto elementDto) {
        for (int dtuIndex = 1; dtuIndex <= ptusPerDay; dtuIndex++) {
            addElementDtuData(elementDto, dtuIndex, loadPerDtu.get(dtuIndex));
        }
    }

    private void addElementDtuData(ElementDto elementDto, Integer ptuIndex, BigInteger load) {
        ElementDtuDataDto elementDtuDataDto = new ElementDtuDataDto();
        elementDtuDataDto.setDtuIndex(ptuIndex);

        // set average load production / consumption
        if (load.compareTo(BigInteger.ZERO) < 0) {
            elementDtuDataDto.setProfileAverageProduction(load.abs());
            elementDtuDataDto.setProfileAverageConsumption(BigInteger.ZERO);
        } else {
            elementDtuDataDto.setProfileAverageProduction(BigInteger.ZERO);
            elementDtuDataDto.setProfileAverageConsumption(load);
        }

        elementDtuDataDto.setProfilePotentialFlexProduction(elementDtuDataDto.getProfileAverageProduction().negate());
        elementDtuDataDto.setProfilePotentialFlexConsumption(elementDtuDataDto.getProfileAverageConsumption().negate());

        elementDto.getElementDtuData().add(elementDtuDataDto);
    }


    private Map<Integer, BigInteger> fetchUncontrolledLoad(List<PbcStubDataDto> pbcStubDataDtos) {
        return pbcStubDataDtos.stream()
                .collect(Collectors.toMap(pbcStubDataDto -> pbcStubDataDto.getPtuContainer().getPtuIndex(),
                        pbcStubDataDto -> BigInteger.valueOf(Math.round(pbcStubDataDto.getCongestionPointAvg()))));
    }

    private Map<Integer, BigInteger> fetchPVLoadForecast(List<PbcStubDataDto> pbcStubDataDtos) {
        return pbcStubDataDtos.stream()
                .collect(Collectors.toMap(pbcStubDataDto -> pbcStubDataDto.getPtuContainer().getPtuIndex(),
                        pbcStubDataDto -> BigInteger.valueOf(Math.round(pbcStubDataDto.getPvLoadForecast()))));
    }


    private BigInteger fetchADSLoadConstant() {
        Random random = new Random();
        BigInteger load = BigInteger.valueOf(random.nextInt(MAGIC_DEVICE_MAX - MAGIC_DEVICE_MIN) + MAGIC_DEVICE_MIN);
        return random.nextBoolean() ? load : load.negate();
    }


    private Map<Integer, BigInteger> createADSLoadMap(LocalDate period, Integer timeSize) {
        Integer timeSlotsPerDay = PtuUtil.getNumberOfPtusPerDay(period, timeSize);
        final BigInteger adsLoadConstant = fetchADSLoadConstant();
        return IntStream.rangeClosed(1, timeSlotsPerDay).mapToObj(Integer::valueOf)
                .collect(Collectors.toMap(Function.identity(), i -> adsLoadConstant));
    }
    */

    private Map<Integer, BigInteger> negateMap(Map<Integer, BigInteger> powerMap) {
        return powerMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().negate()));
    }

    private void updatePowerValue(PowerDataDto powerDataDto, Double value) {
        if (value != null) {
            BigInteger power = BigInteger.valueOf((int) Math.round(value));
            updatePowerValue(powerDataDto, power);
        }
    }

    private void updatePowerValue(PowerDataDto powerDataDto, BigInteger power) {
        if (power != null) {
            if (power.compareTo(BigInteger.ZERO) >= 0) {
                powerDataDto.setAverageConsumption(power);
                powerDataDto.setAverageProduction(BigInteger.ZERO);
            } else {
                powerDataDto.setAverageConsumption(BigInteger.ZERO);
                powerDataDto.setAverageProduction(power.abs());
            }
        }
    }

}
