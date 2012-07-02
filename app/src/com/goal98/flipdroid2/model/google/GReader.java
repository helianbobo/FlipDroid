/****
 *
 *
 *
 *
 *****/

package com.goal98.flipdroid2.model.google;

import com.goal98.flipdroid2.exception.NoMoreStatusException;
import com.goal98.flipdroid2.exception.NoNetworkException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GReader {
    private static String OURHAREURL = "http://www.google.com/reader/shared/rzshang";
    private static String GOOGLE_READER = "https://www.google.com/accounts/ClientLogin";
    private String email;
    private String password;
    private String auth;
    private String sid;
    private String auth_params;
    private String token;
    private StringBuilder allsubscriptionlist;
    private StringBuilder taglist;
    private BasicHttpParams params;
    private List<NameValuePair> paramsNameValuePairs;
    private boolean isGetSidAndAuth = false;
    private HttpGet getreadinglistmethod;
    private InputStream getreadingliststream;
    private StringBuilder getreadingliststring;
    List<RssParser.Item> items;
    List<GreaderListItem> greaderlistItems;
    int pagenumber = 1;
    int everypage = 20;
    int fromindex = 0;

    private int loginstate = 0;

    RssParser.RssFeed feed;

    public void setEveryPage(int everypage) {
        this.everypage = everypage;
    }

    public void getOurShare() {
        // todo

    }

    public GReader(String sid, String auth) {
        this.sid = sid;
        this.auth = auth;
    }

    public GReader() {

    }

    public void getSidAndAuth(String email, String password) throws HttpException, IOException {
        setEmail(email);
        setPassword(password);
        setAuth_params();

        String sidtag = "SID=";
        String authtag = "Auth=";

        HttpPost post = new HttpPost(GOOGLE_READER);
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");


        HttpEntity entity = new UrlEncodedFormEntity(paramsNameValuePairs, HTTP.UTF_8);
        post.setEntity(entity);
        HttpResponse response = null;

        HttpClient httpclient = new DefaultHttpClient();
        response = httpclient.execute(post);
        int result = response.getStatusLine().getStatusCode();
        ////System.out.println("$$$$$");
        //////System.out.println(result);
        if (result == 200) {
            loginstate = 200;

            String[] temps = EntityUtils.toString(response.getEntity()).split("\n");
            ////System.out.println(temps);
            for (String temp : temps) {
                if (temp.startsWith(sidtag))
                    this.sid = temp.substring(sidtag.length());
                else if (temp.startsWith(authtag))
                    this.auth = temp.substring(authtag.length());
            }
            isRight();
        }
        post.abort();
        httpclient.getConnectionManager().shutdown();
        httpclient = null;
    }

    public void requestToken() throws ParseException, HttpException,
            IOException {
        if (!this.isGetSidAndAuth)
            return;
        String url = "https://www.google.com/reader/api/0/token?client=scroll&ck=";

        // HttpClient httpclient = new DefaultHttpClient();
        DefaultHttpClient httpclient = new DefaultHttpClient();
        String timestap = String.valueOf(getTimestamp());
        HttpGet get = new HttpGet(url + timestap);
        get.setHeader("Authorization", "GoogleLogin auth=" + auth);
        get.setHeader("Cookie", "SID=" + sid);
        HttpResponse response = null;
        response = httpclient.execute(get);
        int result = response.getStatusLine().getStatusCode();
        //////System.out.println(result);
        if (result == 200) {
            // this.token = get.getResponseBodyAsString().substring(2);
            ////System.out.println("2222222222222");
            this.token = EntityUtils.toString(response.getEntity()).substring(2);
            ////System.out.println(this.token);
        }
        // ////System.out.println(this.token);
        get.abort();
        httpclient.getConnectionManager().shutdown();
        httpclient = null;
    }

    // taglist
    private StringBuilder requestList(String url) throws HttpException,
            IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);
        get.setHeader("Authorization", "GoogleLogin auth=" + auth);
        get.setHeader("Cookie", "SID=" + sid);
        get.setHeader("accept-encoding", "gzip, deflate");
        HttpResponse response = null;
        response = httpclient.execute(get);
        int result = response.getStatusLine().getStatusCode();
        StringBuilder requestListSB;
        if (result == 200) {
            // //System.out.println(get.getResponseBodyAsString());

            requestListSB = new StringBuilder(EntityUtils.toString(response.getEntity()));

        } else {
            ////System.out.println("xxx:" + String.valueOf(result));

            requestListSB = null;
        }
        get.abort();
        httpclient.getConnectionManager().shutdown();
        httpclient = null;
        return requestListSB;
    }

    private void requestTagList() throws ParseException, HttpException,
            IOException {
        if (!this.isRight())
            return;
        String url = "https://www.google.com/reader/api/0/tag/list?output=json";
        requestList(url);
    }

    public StringBuilder getTagList() {
        try {
            requestTagList();
            return this.taglist;
        } catch (HttpException e) {
            e.printStackTrace();
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void requestReadingList() throws HttpException, IOException {
        if (!this.isRight())
            return;


        int originalSize = 0;
        if (feed != null) {
            items = feed.getItems();
            originalSize = items.size();
        }
        String url = "https://www.google.com/reader/atom/user/-/state/com.google/reading-list?n="
                + String.valueOf(pagenumber * everypage);

        HttpClient httpclient = new DefaultHttpClient();
        getreadinglistmethod = new HttpGet(url);
        getreadinglistmethod.setHeader("Authorization",
                "GoogleLogin auth=" + auth);
        getreadinglistmethod.setHeader("Cookie", "SID=" + sid);
        getreadinglistmethod.setHeader("accept-encoding",
                "gzip, deflate");
        HttpResponse response = null;
        response = httpclient.execute(getreadinglistmethod);
        int result = response.getStatusLine().getStatusCode();
        //Log.d("GOOGLE_READER", "load result"+result);
        if (result >= 200 && result <= 299) {


            RssParser rp = new RssParser(response.getEntity().getContent());
//            //System.out.println("++++++++++");

//            //System.out.println((rp != null ? 1 : 0));
            rp.parse();
            feed = rp.getFeed();
            try {
                items = feed.getItems();
                if (items.size() == originalSize) {
                    throw new NoMoreStatusException();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new NoNetworkException(e);
            }

            greaderlistItems = new ArrayList<GreaderListItem>();

            for (RssParser.Item item : items) {
                {
                    GreaderListItem greaderlistItem = new GreaderListItem();
                    greaderlistItem.setId(item.id);
                    greaderlistItem.setTitle(item.title);
                    greaderlistItem.setLink(item.link);
                    greaderlistItem.setAuthor(item.author);
                    greaderlistItems.add(greaderlistItem);
                }
            }
            fromindex = (pagenumber - 1) * everypage;
            pagenumber += 1;
            lastModified = new Date();
            // this.getreadingliststring=getreadinglistmethod.getResponseBodyAsString();
        } else {
            throw new NoNetworkException();

        }
        releaseReadingList(getreadinglistmethod);
    }

    public Date getLastModified() {
        return lastModified;
    }

    private Date lastModified = new Date();

    // only get existing greaderlistItems
    public List<GreaderListItem> getAllReadingListItems() {
        return this.greaderlistItems;

    }

    // use when first and getnew,get all article of urgoogle reading
    public boolean loadMore() {
        try {
            requestReadingList();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public List<String> getAllReadingListItemsLinks() {
        List<String> links = new ArrayList<String>();
        if (this.greaderlistItems != null && this.greaderlistItems.size() != 0) {
            for (GreaderListItem greaderlistItem : this.greaderlistItems)
                links.add(greaderlistItem.getLink());
            return links;
        } else
            return null;
    }

    public List<GreaderListItem> getNewRead() {
        return this.greaderlistItems.subList(fromindex, this.greaderlistItems
                .size());

    }

    private void releaseReadingList(HttpGet getMethod) {
        if (!getMethod.isAborted())
            /*
            * try { getreadingliststream.close(); } catch (IOException e) { //
            * TODO Auto-generated catch block e.printStackTrace(); }
            */
            getMethod.abort();
    }

    // AllSubscriptionList
    private void requestAllSubscriptionList() throws ParseException,
            HttpException, IOException {
        if (!this.isRight())
            return;
        String url = "https://www.google.com/reader/api/0/subscription/list?output=json";
        this.allsubscriptionlist = requestList(url);
    }

    public StringBuilder getAllSubscriptionList() {
        try {
            requestAllSubscriptionList();
            return new StringBuilder(this.allsubscriptionlist);
        } catch (HttpException e) {
            e.printStackTrace();
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public String getToken() {
        return this.token;
    }

    private boolean isRight() {

        if (!stringIsEmpty(this.sid) && !stringIsEmpty(this.auth))
            isGetSidAndAuth = true;
        else {
            isGetSidAndAuth = false;
            //System.out.printf("isGetSidAndAuth=false");
        }
        return this.isGetSidAndAuth;

    }

    public boolean isLogin() {
        if (this.loginstate == 200 && isRight())
            return true;
        else
            return false;

    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    private void setAuth_params() {
        auth_params = "accountType=HOSTED_OR_GOOGLE&Email=" + this.email
                + "&Passwd=" + this.password + "&service=reader&source=SRZ-gr";

        this.params = new BasicHttpParams();
        this.params.setParameter("accountType", "HOSTED_OR_GOOGLE");
        this.params.setParameter("Email", this.email);
        this.params.setParameter("Passwd", this.password);
        this.params.setParameter("service", "reader");
        this.params.setParameter("source", "SRZ-gr");
        this.paramsNameValuePairs = new ArrayList<NameValuePair>();

        paramsNameValuePairs.add(new BasicNameValuePair("accountType",
                "HOSTED_OR_GOOGLE"));
        paramsNameValuePairs.add(new BasicNameValuePair("Email", this.email));
        paramsNameValuePairs.add(new BasicNameValuePair("Passwd", this.password));
        paramsNameValuePairs.add(new BasicNameValuePair("service", "reader"));
        paramsNameValuePairs.add(new BasicNameValuePair("source", "SRZ-gr"));

    }

    private long getTimestamp() throws ParseException {
        Date date1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                .parse("2009/12/11 00:00:00");
        Date date2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                .parse("1970/01/01 08:00:00");
        long l = date1.getTime() - date2.getTime() > 0 ? date1.getTime()
                - date2.getTime() : date2.getTime() - date1.getTime();
        long rand = (int) (Math.random() * 1000);
        return rand;
    }

    private boolean stringIsEmpty(String str) {
        if (str == null || str.equals(""))
            return true;
        else
            return false;

    }

    /*
     * private void requestReadingListT() throws HttpException, IOException { if
     * (!this.isRight()) return;
     * 
     * String url =
     * "https://www.google.com/reader/atom/user/-/state/com.google/reading-list?n="
     * + String.valueOf(pagenumber * everypage);
     * 
     * HttpClient httpclient = new HttpClient(); getreadinglistmethod = new
     * GetMethod(url); getreadinglistmethod.setRequestHeader("Authorization",
     * "GoogleLogin auth=" + auth);
     * getreadinglistmethod.setRequestHeader("Cookie", "SID=" + sid);
     * getreadinglistmethod.setRequestHeader("accept-encoding",
     * "gzip, deflate"); int result =
     * httpclient.executeMethod(getreadinglistmethod); if (result == 200) {
     * 
     * //System.out.println(getreadinglistmethod
     * .getResponseBodyAsStream().toString());
     * 
     * 
     * InputStreamReader isr = new InputStreamReader(getreadinglistmethod
     * .getResponseBodyAsStream(), "UTF-8"); java.io.BufferedReader br = new
     * java.io.BufferedReader(isr); StringBuffer sb = new StringBuffer(); String
     * tempbf; while ((tempbf = br.readLine()) != null) { sb.append(tempbf);
     * sb.append("\r\n"); ////System.out.println("***********");
     * ////System.out.println(tempbf); } isr.close();
     * 
     * //
     * this.getreadingliststring=getreadinglistmethod.getResponseBodyAsString();
     * } else { //System.out.println("xxx:" + String.valueOf(result));
     * 
     * } releaseReadingList(getreadinglistmethod); }
     */

}
