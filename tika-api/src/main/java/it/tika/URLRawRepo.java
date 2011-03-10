package it.tika;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import de.l3s.boilerpipe.sax.HTMLDocument;
import de.l3s.boilerpipe.sax.HTMLFetcher;
import it.tika.exception.URLRepoException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.zip.GZIPInputStream;

public class URLRawRepo {

    private static URLRawRepo instance;

    private URLRawRepo(){

    }

    public static URLRawRepo getInstance(){
        if(instance == null){
            instance = new URLRawRepo();
        }
        return instance;
    }

    public TextDocument fetch(String urlStr) throws URLRepoException{
        TextDocument doc = null;
        try {
            URL url;
            url = new URL(urlStr);

            final InputSource is = fetch(url).toInputSource();

            final BoilerpipeSAXInput in = new BoilerpipeSAXInput(is);
            doc = in.getTextDocument();
        } catch (IOException e) {
            throw new URLRepoException(e);
        } catch (SAXException e) {
            throw new URLRepoException(e);
        } catch (BoilerpipeProcessingException e) {
            throw new URLRepoException(e);
        }
        return doc;
    }

    //Copy from HTMLFetcher to fix encoding bug
    public HTMLDocument fetch(final URL url) throws IOException {
		final URLConnection conn = url.openConnection();
		final String charset = conn.getContentEncoding();

		Charset cs = Charset.forName("UTF-8");
		if (charset != null) {
			try {
				cs = Charset.forName(charset);
			} catch (UnsupportedCharsetException e) {
				// keep default
			}
		}

		InputStream in = conn.getInputStream();

		final String encoding = conn.getContentEncoding();
		if(encoding != null) {
			if("gzip".equalsIgnoreCase(encoding)) {
				in = new GZIPInputStream(in);
			} else {
				System.err.println("WARN: unsupported Content-Encoding: "+encoding);
			}
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[4096];
		int r;
		while ((r = in.read(buf)) != -1) {
			bos.write(buf, 0, r);
		}
		in.close();

		final byte[] data = bos.toByteArray();

		return new HTMLDocument(data, cs);
	}

}
