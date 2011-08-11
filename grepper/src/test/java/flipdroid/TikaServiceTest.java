package flipdroid;

import it.tika.TikaException;
import it.tika.TikaResponse;
import it.tika.TikaService;
import it.tika.TikaRequest;
import junit.framework.TestCase;

import java.net.MalformedURLException;
import java.text.ParseException;

/**
 * Unit test for simple App.
 */
public class TikaServiceTest
        extends TestCase {

    public void testApp() throws MalformedURLException, ParseException, TikaException {
        TikaService service = new TikaService();
        TikaRequest request = new TikaRequest();
        request.setUrl("http://www.ifanr.com/48882");
//        TikaResponse response = service.
//        assertTrue(response.isSuccess());
//        assertNotNull(response.getContent());
//        assertNotNull(response.getTitle());
//        assertNotNull(response.getImages());
    }
}
