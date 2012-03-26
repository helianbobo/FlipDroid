package it.tika;

import com.goal98.tika.common.URLConnectionUtil;
import com.goal98.tika.common.URLRawRepo;
import com.goal98.tika.common.URLRepoException;
import flipdroid.grepper.URLAbstract;
import org.apache.commons.io.FileUtils;
import org.apache.thrift.TException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

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

        URLAbstract result = tika.extract(urlDecoded, !request.isUseCache(), request.getReferencedFrom());
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

    public static void main(String[] args) throws TException, TikaException, IOException {
        TikaServiceImpl tikaService = new TikaServiceImpl();
        TikaRequest request = new TikaRequest();
        request.setUrl("http://www.36kr.com/p/92876.html");
        ExecutorService executorService =  Executors.newFixedThreadPool(1);
        TikaResponse response = tikaService.fire(request);
        System.out.println(response.getContent());
//        String urls = FileUtils.readFileToString(new File("/Users/jleo/Downloads/rar/tk.txt"));
//        String[] split = urls.split("\n");
//        int idx = 10030;
//        for (int i = 10031; i < split.length; i++) {
//            try {
//                final String s = split[i];
//                idx++;
//                final int fileName = idx;
//                System.out.println(idx);
//                //request.setUrl("http://tk.med66.com/tk25/25486.shtml");
//
//                executorService.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            grep(fileName,s);
//                        } catch (IOException e) {
//                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                        }
//                    }
//                });
//            } catch (Exception e) {
//                continue;
//            }
//        }
//        System.out.println(response.getContent());
    }

//    private static boolean grep(int idx, String urlStr) throws IOException {
//        HttpURLConnection conn = null;
//        int count = 0;
//        int responseCode = 0;
//        boolean isText = false;
//        while (count < 3) {
//            try {
////                        LOG.info("trying "+urlString);
//                URL url = new URL(urlStr);
//                conn = URLConnectionUtil.decorateURLConnection(url);
//
//                responseCode = conn.getResponseCode();
//                isText = conn.getContentType().toUpperCase().indexOf("TEXT/HTML") != -1;
////                        LOG.info("responseCode " + responseCode);
//                if (responseCode >= 200 && responseCode <= 299) {
//                    break;
//                } else {
//                    count++;
//                }
//            } catch (IOException e) {
//                count++;
//            }
//        }
//        if (responseCode < 200 || responseCode > 299 || !isText) {
//            return true;
//        }
//        String originalURLString = conn.getURL().toString();
//
//        byte[] rawBytes = URLRawRepo.getInstance().fetch(conn);
//        String content = new String(rawBytes, "gb2312");
//        int start = content.indexOf("<div class=\"content\" id=\"fontzoom\">");
//        int end = content.indexOf("<div class=\"contpage\">");
////            TikaResponse response = tikaService.fire(request);
////            System.out.println(response);
//        FileUtils.writeStringToFile(new File("/Users/jleo/Downloads/tk/tk" + idx + ".txt"), content.substring(start, end));
//        return false;
//    }
}
