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

package energy.usef.vudp.pbcfeeder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import energy.usef.vudp.pbcfeeder.dto.Connection;
import energy.usef.vudp.pbcfeeder.dto.Settings;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RestService for fetching JSON-data from PBCFeeder-deployment. The endpoint of the PBCFeeder is configured within the main
 * configuration file.
 */
public class PbcFeederClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(PbcFeederClient.class);

    @Inject
    private VudpConfigPbcFeeder config;

    public List<Connection> findConnections() {
        String pbcEndpoint = config.getProperty(VudpConfigPbcFeederParam.PBC_FEEDER_ENDPOINT) + "/connections";

        String value = get(pbcEndpoint);
        ObjectMapper mapper = new ObjectMapper();

        List<Connection> connections = new ArrayList<>();

        try {
            connections = mapper.readValue(value, new TypeReference<List<Connection>>() { });
        } catch (IOException e) {
            LOGGER.error("Exception caught: {}", e);
        }

        return connections;
    }

    public Map<String,Object> findSettings() {
        String pbcEndpoint = config.getProperty(VudpConfigPbcFeederParam.PBC_FEEDER_ENDPOINT) + "/settings";

        String value = get(pbcEndpoint);
        ObjectMapper mapper = new ObjectMapper();

        Map<String,Object> settings = null;

        try {
            settings = mapper.readValue(value, new TypeReference<Map<String,Object>>() { });
        } catch (IOException e) {
            LOGGER.error("Exception caught: {}", e);
        }

        return settings;
    }

    /**
     * Perform GET request for given URL and return JSON-value as String.
     *
     * @param url
     * @return
     */
    public String get(String url) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(url);
        Response response = target.request().buildGet().invoke();
        String value = response.readEntity(String.class);
        response.close();
        return value;
    }
}
