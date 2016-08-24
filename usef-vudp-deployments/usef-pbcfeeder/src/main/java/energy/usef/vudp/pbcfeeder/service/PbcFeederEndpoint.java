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

package energy.usef.vudp.pbcfeeder.service;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import energy.usef.vudp.pbcfeeder.dto.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import energy.usef.vudp.pbcfeeder.DataRepository;
import energy.usef.vudp.pbcfeeder.dto.Data;
import energy.usef.vudp.pbcfeeder.dto.Connection;

import java.util.List;

/**
 * Endpoint to request data from the PBCFeeder.
 */
@Path("/PBCFeeder")
public class PbcFeederEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(PbcFeederEndpoint.class);

    @Inject
    private DataRepository dataRepository;

    @GET
    @Path("/connections")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Connection> getConnections() {
        LOGGER.info("Retrieving Data");
        return dataRepository.findData().getConnection();
    }

    @GET
    @Path("/settings")
    @Produces(MediaType.APPLICATION_JSON)
    public Settings getSettings() {
        LOGGER.info("Retrieving Data");
        return dataRepository.findData().getSettings();
    }

}
