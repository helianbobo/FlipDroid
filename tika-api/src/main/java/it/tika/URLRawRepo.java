package it.tika;

import it.tika.exception.URLRepoException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

public class URLRawRepo {

    private static URLRawRepo instance;


    private URLRawRepo() {

    }

    public static URLRawRepo getInstance() {
        if (instance == null) {
            instance = new URLRawRepo();
        }
        return instance;
    }

    public byte[] fetch(String urlStr) throws URLRepoException, IOException {
        URL url = new URL(urlStr);
        final URLConnection conn = url.openConnection();

        InputStream in = conn.getInputStream();

        final String encoding = conn.getContentEncoding();
        if (encoding != null) {
            if ("gzip".equalsIgnoreCase(encoding)) {
                in = new GZIPInputStream(in);
            } else {
                System.err.println("WARN: unsupported Content-Encoding: " + encoding);
            }
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int r;
        try {
            while ((r = in.read(buf)) != -1) {
                bos.write(buf, 0, r);
            }
        } finally {
            in.close();
        }

        return bos.toByteArray();
    }


}
