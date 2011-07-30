package com.goal98.flipdroid.model.rss;

import com.goal98.flipdroid.db.SourceDB;
import com.goal98.flipdroid.model.OnSourceLoadedListener;
import com.goal98.flipdroid.util.Constants;
import com.goal98.flipdroid.util.EncodingDetector;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;
import weibo4j.org.json.JSONArray;
import weibo4j.org.json.JSONObject;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class RssParser extends DefaultHandler {
    private String urlString;
    private RssFeed rssFeed;
    private StringBuilder text;
    private Item item;
    private boolean imgStatus;
    private InputStream urlInputStream;
    private OnSourceLoadedListener listener;

    public RssParser(String url) {
        this.urlString = url;
        this.text = new StringBuilder();
    }

    public RssParser(InputStream is) {
        this.urlInputStream = is;
        this.text = new StringBuilder();
    }

    public void parse() {
        try {
            if (this.urlInputStream == null) {
                this.urlInputStream = createURLInputStream();
            }
            SAXParserFactory spf = null;
            SAXParser sp = null;

            spf = SAXParserFactory.newInstance();
//            if (spf != null) {
//                sp = spf.newSAXParser();
//                String raw = IOUtils.toString(urlInputStream);
//                String encoding = EncodingDetector.detect(new ByteArrayInputStream(raw.getBytes()));
//                InputSource inputSource = new InputSource();
//                inputSource.setByteStream(new ByteArrayInputStream(raw.getBytes(encoding)));
//                inputSource.setSystemId(urlString);
//                sp.parse(inputSource, this);
//            }
            if (spf != null) {
                sp = spf.newSAXParser();
                byte[] contents = getContentInBytes(this.urlInputStream);

                String encoding = EncodingDetector.detect(new ByteArrayInputStream(contents));
                if (listener != null)
                    listener.onLoaded(new String(contents, encoding));
//                String content = new String(contents, encoding);
                InputStreamReader streamReader = new InputStreamReader(new ByteArrayInputStream(contents), encoding);
                InputSource inputSource = new InputSource(streamReader);
                inputSource.setSystemId(urlString);
                sp.parse(inputSource, this);
            }
        } catch (Exception
                e) {
            ////System.out.println("Exception: " + e);
            e.printStackTrace();
        } finally {
            try {
                if (urlInputStream != null)
                    urlInputStream.close();
            } catch (Exception e) {
            }
        }
    }

    public byte[] getContentInBytes(InputStream is) throws IOException {
        return IOUtils.toByteArray(is);
    }

    public String getContent() throws IOException {
        InputStream is = createURLInputStream();
        byte[] contentInBytes = getContentInBytes(is);
        String encoding = EncodingDetector.detect(new ByteArrayInputStream(contentInBytes));
        return new String(contentInBytes, encoding);
    }

    public InputStream createURLInputStream() throws IOException {
        URL url = new URL(this.urlString);
        _setProxy(); // Set the proxy if needed
        return url.openConnection().getInputStream();
    }

    public RssFeed getFeed() {
        return (this.rssFeed);
    }

    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) {

        if (qName.equalsIgnoreCase("channel"))
            this.rssFeed = new RssFeed();
        else if (qName.equalsIgnoreCase("item") && (this.rssFeed != null)) {
            this.item = new Item();
            this.rssFeed.addItem(this.item);
        } else if (qName.equalsIgnoreCase("image") && (this.rssFeed != null))
            this.imgStatus = true;
    }

    public void endElement(String uri, String localName, String qName) {
        if (this.rssFeed == null)
            return;

        if (qName.equalsIgnoreCase("item"))
            this.item = null;

        else if (qName.equalsIgnoreCase("image"))
            this.imgStatus = false;

        else if (qName.equalsIgnoreCase("title")) {
            if (this.item != null) this.item.title = this.text.toString().trim();
            else if (this.imgStatus) this.rssFeed.imageTitle = this.text.toString().trim();
            else this.rssFeed.title = this.text.toString().trim();
        } else if (qName.equalsIgnoreCase("link")) {
            if (this.item != null) this.item.link = this.text.toString().trim();
            else if (this.imgStatus) this.rssFeed.imageLink = this.text.toString().trim();
            else this.rssFeed.link = this.text.toString().trim();
        } else if (qName.equalsIgnoreCase("description")) {
            if (this.item != null)
                this.item.description = this.text.toString().trim();
            else if (imgStatus) {
                this.rssFeed.imageDescription = this.text.toString().trim();
            } else this.rssFeed.description = this.text.toString().trim();
        } else if (qName.equalsIgnoreCase("url") && this.imgStatus)
            this.rssFeed.imageUrl = this.text.toString().trim();
        else if (qName.equalsIgnoreCase("author")) {
            if (this.item != null) this.item.author = this.text.toString().trim();
        } else if (qName.equalsIgnoreCase("language"))
            this.rssFeed.language = this.text.toString().trim();

        else if (qName.equalsIgnoreCase("generator"))
            this.rssFeed.generator = this.text.toString().trim();

        else if (qName.equalsIgnoreCase("copyright"))
            this.rssFeed.copyright = this.text.toString().trim();

        else if (qName.equalsIgnoreCase("pubDate") && (this.item != null))
            this.item.pubDate = this.text.toString().trim();

        else if (qName.equalsIgnoreCase("category") && (this.item != null))
            this.rssFeed.addItem(this.text.toString().trim(), this.item);

        this.text.setLength(0);
    }

    public void characters(char[] ch, int start, int length) {
        this.text.append(ch, start, length);
    }

    public static void _setProxy()
            throws IOException {
        Properties sysProperties = System.getProperties();
        sysProperties.put("proxyHost", "<Proxy IP Address>");
        sysProperties.put("proxyPort", "<Proxy Port Number>");
        System.setProperties(sysProperties);
    }

    public void addOnLoadListener(OnSourceLoadedListener listener) {
        this.listener = listener;
    }

    public static class RssFeed {
        public String title;
        public String description;
        public String link;
        public String language;
        public String generator;
        public String copyright;
        public String imageUrl;
        public String imageTitle;
        public String imageLink;
        public String imageDescription;

        public ArrayList<Item> getItems() {
            return items;
        }

        private ArrayList<Item> items = new ArrayList<Item>();
        private HashMap<String, ArrayList<Item>> category;

        public void addItem(Item item) {
            this.items.add(item);
        }

        public void addItem(String category, Item item) {
            if (this.category == null)
                this.category = new HashMap<String, ArrayList<Item>>();
            if (!this.category.containsKey(category))
                this.category.put(category, new ArrayList<Item>());
            this.category.get(category).add(item);
        }
    }


    public static class Item {
        public String title;
        public String description;
        public String link;
        public String pubDate;
        public String id;
        public String author;

        public String toString() {
            return (this.title + ": " + this.pubDate + "n" + this.description);
        }
    }


    public static void main(String[] args) {
        String url = "\t\n" +
                "http://news.baidu.com/ns?word=%D7%E3%C7%F2&tn=newsrss&sr=0&cl=1&rn=20&ct=0";
        String cat = "手机";

        String template = ",{\n" +
                "        \"name\": \"$name\",\n" +
                "        \"id\": \"$id\",\n" +
                "        \"desc\": \"$desc\",\n" +
                "        \"image_url\": \"$imageUrl\",\n" +
                "        \"content_url\":\"$contentUrl\",\n" +
                "        \"cat\":\"$cat\"\n" +
                "    }";

        RssParser rp = new RssParser(url);
        try {
            rp.parse();
            RssParser.RssFeed feed = rp.getFeed();
            final String title = feed.title;
            final String description = feed.description == null ? "" : feed.description;
            final String imageUrl = feed.imageUrl == null ? "" : feed.imageUrl;

            final String contentUrl = url;
            String json = template.replace("$name", title).replace("$desc", description).replace("$imageUrl", imageUrl.trim()).replace("$contentUrl", contentUrl.trim());
            System.out.println(json);


            File f = new File("D:\\myprojects\\android\\helianbobo-FlipDroid\\app\\assets\\RSS_RECOMMAND_SOURCE_DATA.json");
            String content = FileUtils.readFileToString(f);
            JSONArray array = new JSONArray(content);
            int id = 0;
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = (JSONObject) array.get(i);
                id = Integer.valueOf((String) object.get("id"));
            }
            id++;
            json = json.replace("$id", id + "");
            json = json.replace("$cat", cat);
            content = content.replace("]", "") + "\n" + json + "]";
            FileUtils.writeStringToFile(f, content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}