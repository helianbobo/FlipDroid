package it.tika;

import flipdroid.grepper.EncodingDetector;
import flipdroid.grepper.URLAbstract;
import flipdroid.grepper.WebpageExtractor;
import flipdroid.grepper.extractor.image.TikaImageService;
import flipdroid.grepper.extractor.raw.URLRawRepo;
import org.apache.thrift.TException;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 8/10/11
 * Time: 10:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class TikaServiceImpl implements TikaService.Iface{
    public TikaResponse fire(TikaRequest request) throws TikaException, TException {
        WebpageExtractor extractor = new WebpageExtractor(new TikaImageService());
        TikaResponse response = new TikaResponse();

        try {
            String urlDecoded = java.net.URLDecoder.decode(request.getUrl(), "UTF-8");
            byte[] rawBytes = URLRawRepo.getInstance().fetch(urlDecoded);
            String charset = EncodingDetector.detect(new BufferedInputStream(new ByteArrayInputStream(rawBytes)));
            Charset cs = null;
            if (charset != null)
                cs = Charset.forName(charset);
            else {
                try {
                    cs = Charset.forName("utf-8");
                } catch (UnsupportedCharsetException e) {
                }
            }

            if (rawBytes == null) {
                response.setSuccess(false);
            } else {
                URLAbstract result = new URLAbstract(rawBytes, cs);
                result.setUrl(urlDecoded);
                result = extractor.extract(result);
                response.setContent(result.getContent());
                response.setImages(result.getImages());
                response.setTitle(result.getTitle());
                response.setSuccess(true);
            }
            return response;
        } catch (Exception e) {
            throw new TikaException(e.getMessage());
        }
    }
}
