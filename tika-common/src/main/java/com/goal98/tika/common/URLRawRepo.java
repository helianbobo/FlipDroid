package com.goal98.tika.common;


import com.goal98.tika.common.URLConnectionUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

public class URLRawRepo {

    private static URLRawRepo instance = new URLRawRepo();


    private URLRawRepo() {

    }

    public static URLRawRepo getInstance() {
        return instance;
    }

    public byte[] fetch(final URLConnection conn) throws URLRepoException, IOException {
        ByteArrayOutputStream bos = null;
        InputStream in = null;
        int retryCount = 0;
        try {
            if (retryCount >= 3)
                return null;

            retryCount++;
            in = conn.getInputStream();

            final String encoding = conn.getContentEncoding();
            if (encoding != null) {
                if ("gzip".equalsIgnoreCase(encoding)) {
                    in = new GZIPInputStream(in);
                } else {
                    System.err.println("WARN: unsupported Content-Encoding: " + encoding);
                }
            }

            bos = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int r;
            try {
                while ((r = in.read(buf)) != -1) {
                    bos.write(buf, 0, r);
                }
            } finally {
                in.close();
            }
        } catch (IOException e) {
            retryCount++;
        } finally {
            if (in != null)
                in.close();
        }
        return bos.toByteArray();
    }


}
