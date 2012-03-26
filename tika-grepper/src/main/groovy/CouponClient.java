
import com.goal98.tika.common.URLRawRepo;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 12-3-20
 * Time: 下午12:27
 * To change this template use File | Settings | File Templates.
 */
public class CouponClient {

    private String TAG = this.getClass().getName();

    private String host;

    public CouponClient(String host) {
        this.host = host;
    }

    public List<String> getCoupon() {
        List<String> coupons = new ArrayList<String>();
        try {
            String urlStr = host + "/coupons/";
            String parameter = "ver=1.0.1";
            System.out.println(read(urlStr, parameter));

        } catch (Exception e) {
            return coupons;
        }
        return coupons;
    }

    public String read(String urlStr, String parameter) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection uc = (HttpURLConnection) url.openConnection();
        uc.setDoOutput(true);

        OutputStream raw = uc.getOutputStream();
        OutputStream buf = new BufferedOutputStream(raw);
        OutputStreamWriter out = new OutputStreamWriter(buf, "utf-8");
        try {
            out.write(parameter);
            out.flush();
        } finally {
            out.close();
        }

        final int responseCode = uc.getResponseCode();
        if (responseCode >= 200 && responseCode <= 299) {
            byte[] response = URLRawRepo.getInstance().fetch(uc);
            final String s = new String(response, "utf-8");
            if (s != null && (s.startsWith("{") || s.startsWith("["))) {// json
                return s;
            }
            return null;
        } else if (responseCode == 304) {
            return null;
        } else
            return null;
    }

    public static void main(String[] args) {
        CouponClient couponClient = new CouponClient("http://www.uutuu.com/api/newzealandapp");
        couponClient.getCoupon();
    }
}
