package com.goal98.flipdroid.model.rss;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class RssParser extends DefaultHandler {
    private String urlString;
    private RssFeed rssFeed;
    private StringBuilder text;
    private Item item;
    private boolean imgStatus;
    private InputStream urlInputStream;

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
                URL url = new URL(this.urlString);
                _setProxy(); // Set the proxy if needed
                urlInputStream = url.openConnection().getInputStream();
            }
            SAXParserFactory spf = null;
            SAXParser sp = null;

            spf = SAXParserFactory.newInstance();
            if (spf != null) {
                sp = spf.newSAXParser();
                sp.parse(urlInputStream, this);
            }
        } catch (Exception
                e) {
            System.out.println("Exception: " + e);
            e.printStackTrace();
        } finally {
            try {
                if (urlInputStream != null)
                    urlInputStream.close();
            } catch (Exception e) {
            }
        }
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
            if (this.item != null) this.item.description = this.text.toString().trim();
            else this.rssFeed.description = this.text.toString().trim();
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

}