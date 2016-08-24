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

import energy.usef.agr.dto.ConnectionPortfolioDto;
import energy.usef.agr.dto.ElementDto;
import energy.usef.vudp.pbcfeeder.PbcFeederClient;
import energy.usef.vudp.pbcfeeder.dto.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class PbcFeederServiceTest {

    @Mock
    PbcFeederClient pbcFeederClient;
    private PbcFeederService pbcFeederService;

    @Before
    public void setUp() throws Exception {
        pbcFeederService = new PbcFeederService();
        Whitebox.setInternalState(pbcFeederService, "pbcFeederClient", pbcFeederClient);
    }

    @Test
    public void testFillElementsFromPbcFeederWithoutMissingConnections(){
        testFillElementsFromPBCFeeder(false);
    }

    @Test
    public void testFillElementsFromPbcFeederWithMissingConnections(){
        testFillElementsFromPBCFeeder(true);
    }

    private void testFillElementsFromPBCFeeder(boolean missingConnection) {
        // variables and mocking
        List<ConnectionPortfolioDto> connectionPortfolio = Arrays
                .asList(new ConnectionPortfolioDto("ean.0000000001"), new ConnectionPortfolioDto("ean.0000000002"));
        final LocalDate period = new LocalDate(2016, 1, 2);
        final Integer ptusPerDay = 12;
        final Integer pbcDay = 1;
        Mockito.when(pbcFeederClient.findConnections())
                .then(call -> {
                    List<Connection> connections = new ArrayList<Connection>();

                    connections.add(buildConnection("ean.0000000001", ptusPerDay, pbcDay));
                    if(!missingConnection){connections.add(buildConnection("ean.0000000002", ptusPerDay, pbcDay));}
                    connections.add(buildConnection("ean.0000000003", ptusPerDay, pbcDay));

                    return connections;
                });
        Mockito.when(pbcFeederClient.findSettings())
                .then(call -> {
                    Map settings = new HashMap();
                    List<String> pbcDays = new ArrayList<String>();
                    pbcDays.add("0");
                    pbcDays.add("1");
                    pbcDays.add("2");
                    pbcDays.add("3");
                    settings.put("seed",1);
                    settings.put("dayCycle",pbcDays);
                    return settings;
                });

        // invocation
        List<ElementDto> elementList = pbcFeederService.fillElementsFromPBCFeeder(connectionPortfolio, period, ptusPerDay, 120);

        if(missingConnection){
            missingConnnectionAssertions(elementList);
        }
        else{
            noMissingConnnectionAssertions(elementList);
        }
    }

    private void missingConnnectionAssertions(List<ElementDto> elementList) {
        Assert.assertNotNull(elementList);
        Assert.assertEquals(3, elementList.size());
    }

    private void noMissingConnnectionAssertions(List<ElementDto> elementList) {
        Assert.assertNotNull(elementList);

        // 2 connections, 3 elements per connection, so make sure there are 6 elements returned
        Assert.assertEquals(6, elementList.size());

        // make sure that there are exactly 12 elementDtuData objects created per element
        elementList.stream().forEach(elementDto -> {
            Assert.assertEquals(12, elementDto.getElementDtuData().size());
        });

        List<ElementDto> connnectionElementList1 = elementList.stream()
                .filter(elementDto -> elementDto.getConnectionEntityAddress().equals("ean.0000000001"))
                .collect(Collectors.toList());
        List<ElementDto> connnectionElementList2 = elementList.stream()
                .filter(elementDto -> elementDto.getConnectionEntityAddress().equals("ean.0000000002"))
                .collect(Collectors.toList());

        // 3 elements per connection
        Assert.assertEquals(3, connnectionElementList1.size());
        Assert.assertEquals(3, connnectionElementList2.size());

        // exactly 1 element with id PV1 and ADS1 per connection
        Assert.assertEquals(1,
                connnectionElementList1.stream().filter(elementDto -> elementDto.getId().equals("ean.0000000001.PV1")).count());
        Assert.assertEquals(1,
                connnectionElementList1.stream().filter(elementDto -> elementDto.getId().equals("ean.0000000001.ADS1")).count());
        Assert.assertEquals(1,
                connnectionElementList1.stream().filter(elementDto -> elementDto.getId().equals("ean.0000000001.UCL")).count());
        Assert.assertEquals(1,
                connnectionElementList2.stream().filter(elementDto -> elementDto.getId().equals("ean.0000000002.PV1")).count());
        Assert.assertEquals(1,
                connnectionElementList2.stream().filter(elementDto -> elementDto.getId().equals("ean.0000000002.ADS1")).count());
        Assert.assertEquals(1,
                connnectionElementList2.stream().filter(elementDto -> elementDto.getId().equals("ean.0000000002.UCL")).count());

        // check the uncontrolled load in the synthetic data elements
        connnectionElementList1.stream().filter(elementDto -> elementDto.getId().contains(".UCL")).forEach(elementDto -> {
            elementDto.getElementDtuData().forEach(elementDtuDataDto -> {
                Assert.assertNotNull(elementDtuDataDto.getProfileUncontrolledLoad());
                Assert.assertNull(elementDtuDataDto.getProfileAverageConsumption());
                Assert.assertNull(elementDtuDataDto.getProfileAverageProduction());
                Assert.assertNull(elementDtuDataDto.getProfilePotentialFlexConsumption());
                Assert.assertNull(elementDtuDataDto.getProfilePotentialFlexProduction());
                Assert.assertEquals(BigInteger.valueOf(1000), elementDtuDataDto.getProfileUncontrolledLoad());
            });
        });

        // check the average consumption/production load in the synthetic data elements
        connnectionElementList1.stream().filter(elementDto -> !elementDto.getId().contains(".UCL")).forEach(elementDto -> {
            elementDto.getElementDtuData().forEach(elementDtuDataDto -> {
                Assert.assertNull(elementDtuDataDto.getProfileUncontrolledLoad());
                Assert.assertNotNull(elementDtuDataDto.getProfileAverageConsumption());
                Assert.assertNotNull(elementDtuDataDto.getProfileAverageProduction());
                Assert.assertNotNull(elementDtuDataDto.getProfilePotentialFlexConsumption());
                Assert.assertNotNull(elementDtuDataDto.getProfilePotentialFlexProduction());
                Assert.assertEquals(BigInteger.valueOf(1100), elementDtuDataDto.getProfileAverageConsumption());
                Assert.assertEquals(BigInteger.valueOf(1200), elementDtuDataDto.getProfileAverageProduction());
                Assert.assertEquals(BigInteger.valueOf(110), elementDtuDataDto.getProfilePotentialFlexConsumption());
                Assert.assertEquals(BigInteger.valueOf(120), elementDtuDataDto.getProfilePotentialFlexProduction());
            });
        });
    }

    private Connection buildConnection(String ean, Integer ptusPerDay, Integer pbcDay) {
        Connection connection = new Connection();
        connection.setEan(ean);

        Map uncontrolled = new HashMap();
        Map ptuMap = new HashMap();
        for(Integer i = 1; i <= ptusPerDay; i++){
            UncontrolledPower up = new UncontrolledPower();
            up.setForecast(1000l);
            up.setObserved(1000l);
            ptuMap.put(i,up);
        }
        uncontrolled.put(pbcDay,ptuMap);
        connection.setUncontrolled(uncontrolled);

        List<Device> devices = new ArrayList<Device>();
        devices.add(buildDevice("PV1","PV", ean, ptusPerDay, pbcDay));
        devices.add(buildDevice("ADS1","ADS", ean, ptusPerDay, pbcDay));

        connection.setDevices(devices);

        return connection;
    }

    private Device buildDevice(String name, String profile, String ean, Integer ptusPerDay, Integer pbcDay) {
        Device device = new Device();
        device.setName(name);
        device.setProfile(profile);
        device.setForecastDeviation(BigDecimal.valueOf(0,2));
        device.setDtuSize(120);
        device.setEndpoint(ean + "." + name);

        Map deviceForecast = new HashMap();
        Map ptuMap = new HashMap();
        for(int i = 1; i <= ptusPerDay; i++){
            Power power = new Power();
            power.setConsumption(1100l);
            power.setProduction(1200l);
            power.setFlexConsumption(110l);
            power.setFlexProduction(120l);
            ptuMap.put(i,power);
        }
        deviceForecast.put(pbcDay,ptuMap);
        device.setPowerPerDayPerPtu(deviceForecast);

        return device;
    }
}
