--
-- This software source code is provided by the USEF Foundation. The copyright
-- and all other intellectual property rights relating to all software source
-- code provided by the USEF Foundation (and changes and modifications as well
-- as on new versions of this software source code) belong exclusively to the
-- USEF Foundation and/or its suppliers or licensors. Total or partial
-- transfer of such a right is not allowed. The user of the software source
-- code made available by USEF Foundation acknowledges these rights and will
-- refrain from any form of infringement of these rights.
--
-- The USEF Foundation provides this software source code "as is". In no event
-- shall the USEF Foundation and/or its suppliers or licensors have any
-- liability for any incidental, special, indirect or consequential damages;
-- loss of profits, revenue or data; business interruption or cost of cover or
-- damages arising out of or in connection with the software source code or
-- accompanying documentation.
--
-- For the full license agreement see http\://www.usef.info/license.
--

INSERT INTO DISTRIBUTION_SYSTEM_OPERATOR ( ID, DOMAIN) VALUES (1, 'usef-example.com');
INSERT INTO DISTRIBUTION_SYSTEM_OPERATOR ( ID, DOMAIN) VALUES (2, 'usef-dummy.com');

INSERT INTO AGGREGATOR (ID, DOMAIN) VALUES (1, 'tesla.com');
INSERT INTO AGGREGATOR (ID, DOMAIN) VALUES (2, 'mijn-groene-energie.com');
INSERT INTO AGGREGATOR (ID, DOMAIN) VALUES (3, 'ik-ben-een-aggregator.com');

INSERT INTO CONGESTION_POINT (ID, ENTITY_ADDRESS, DISTRIBUTION_SYSTEM_OPERATOR_ID) VALUES (1, 'ea1.1992-01.com.example:gridpoint.4f76ff19-a53b-49f5-84e6', 1);
INSERT INTO CONGESTION_POINT (ID, ENTITY_ADDRESS, DISTRIBUTION_SYSTEM_OPERATOR_ID) VALUES (2, 'ea1.1992-02.com.otherexample:gridpoint.4f76ff19-a53b-49f5-99e9', 2);
INSERT INTO CONGESTION_POINT (ID, ENTITY_ADDRESS, DISTRIBUTION_SYSTEM_OPERATOR_ID) VALUES (3, 'ea1.1992-03.com.otherexample:gridpoint.4f76ff19-a53b-49f5-99e9', 1);

INSERT INTO CONNECTION (ID, ENTITY_ADDRESS, CONGESTION_POINT_ID, AGGREGATOR_ID) VALUES (1, 'ean.871685900012636543', 1, 1);
INSERT INTO CONNECTION (ID, ENTITY_ADDRESS, CONGESTION_POINT_ID, AGGREGATOR_ID) VALUES (2, 'ean.121685900012636999', 1, 1);
INSERT INTO CONNECTION (ID, ENTITY_ADDRESS, CONGESTION_POINT_ID, AGGREGATOR_ID) VALUES (3, 'ean.789685900012636123', 1, 2);
INSERT INTO CONNECTION (ID, ENTITY_ADDRESS, CONGESTION_POINT_ID, AGGREGATOR_ID) VALUES (4, 'ean.673685900012637348', NULL, 2);
INSERT INTO CONNECTION (ID, ENTITY_ADDRESS, CONGESTION_POINT_ID, AGGREGATOR_ID) VALUES (5, 'ean.673685923012637348', 3, 3);
INSERT INTO CONNECTION (ID, ENTITY_ADDRESS, AGGREGATOR_ID) VALUES (6, 'ean.789685900012636129', 3);
INSERT INTO CONNECTION (ID, ENTITY_ADDRESS, AGGREGATOR_ID) VALUES (7, 'ean.673685900012637341', 3);
INSERT INTO CONNECTION (ID, ENTITY_ADDRESS, CONGESTION_POINT_ID) VALUES (8, 'ean.789685901012636129', 1);
INSERT INTO CONNECTION (ID, ENTITY_ADDRESS, CONGESTION_POINT_ID) VALUES (9, 'ean.673685902012637341', 1);

