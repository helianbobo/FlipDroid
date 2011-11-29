package it.tika;

import flipdroid.grepper.URLAbstract;
import org.apache.thrift.TException;

import java.io.UnsupportedEncodingException;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 8/10/11
 * Time: 10:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class TikaServiceImpl implements TikaService.Iface {
    private Tika tika = Tika.getInstance();

    public TikaResponse fire(TikaRequest request) throws TikaException, TException {
        String url = request.getUrl();

        String urlDecoded = null;
        try {
            urlDecoded = java.net.URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }

        URLAbstract result = tika.extract(urlDecoded, false,request.getReferencedFrom());
        if (result == null)
            throw new TikaException();

        result.setUrl(urlDecoded);

        TikaResponse response = new TikaResponse();
        response.setContent(result.getContent());
        response.setImages(result.getImages());
        response.setTitle(result.getTitle());
        response.setSuccess(true);
        return response;
    }

    public static void main(String[] args) throws TException, TikaException {
        TikaServiceImpl tikaService = new TikaServiceImpl();
        TikaRequest request = new TikaRequest();
        request.setUrl("http://blog.sina.com.cn/s/blog_4c5ad7e10102dspq.html");
        TikaResponse response = tikaService.fire(request);
        System.out.println(response.getContent());
    }
}
