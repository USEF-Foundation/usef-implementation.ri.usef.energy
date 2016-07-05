

package energy.usef.vudp.pbcfeeder;

import static org.junit.Assert.assertEquals;

import java.nio.file.Paths;

import org.junit.Test;

import energy.usef.vudp.pbcfeeder.model.Data;

/**
 *
 */
public class DataFactoryTest {

    @Test
    public void testBuildNewData() throws Exception {
        DataFactory dataFactory = new DataFactory(Paths.get("src/test/resources", DataRepository.PBC_FILE_NAME));
        Data data = dataFactory.getData();

        assertEquals(5, data.getConnection().size());
    }
}
