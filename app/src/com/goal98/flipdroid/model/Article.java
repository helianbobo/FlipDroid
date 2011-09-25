package com.goal98.flipdroid.model;

import android.graphics.Bitmap;
import com.goal98.android.ImageLoader;
import com.goal98.flipdroid.util.DeviceInfo;
import com.goal98.flipdroid.view.ExpandableArticleView;
import com.goal98.flipdroid.view.ThumbnailArticleView;

import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Article {
    public boolean isExpandable() {
        return expandable;
    }

    private boolean expandable;

    public Map<String, Bitmap> getImagesMap() {
        return imagesMap;
    }

    private String title;
    private URL portraitImageUrl;
    private String author;
    private String status;
    private String content;
    private URL imageUrl;
    private long statusId;
    private String sourceType;
    private Bitmap image;
    private ThumbnailArticleView.Notifier notifier;
    private int height;
    private Map<String, Bitmap> imagesMap = new HashMap<String, Bitmap>();
    private List<String> images = new ArrayList<String>();
    private int imageWidth;
    private int imageHeight;
    private boolean layoutVertical;
    private float suggestScale;
    private int textHeight;

    public int getTextHeight() {
        return textHeight;
    }

    public void setTextHeight(int textHeight) {
        this.textHeight = textHeight;
    }

    public float getSuggestScale() {
        return suggestScale;
    }

    public void setSuggestScale(float suggestScale) {
        this.suggestScale = suggestScale;
    }

    public boolean isLayoutVertical() {
        return layoutVertical;
    }

    public void setLayoutVertical(boolean layoutVertical) {
        this.layoutVertical = layoutVertical;
    }

    public List<String> getImages() {
        return images;
    }

    public boolean isAlreadyLoaded() {
        return alreadyLoaded;
    }

    private boolean alreadyLoaded;

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public long getStatusId() {
        return statusId;
    }

    public void setStatusId(long statusId) {
        this.statusId = statusId;
    }

    static Pattern urlPattern = Pattern.compile(
            "http://([\\w-]+\\.)+[\\w-]+(/[\\w\\-./?%:~%&=]*)?",
            Pattern.CASE_INSENSITIVE);


    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    private int weight = -1;
    private Date createdDate;

    public boolean hasLink() {
        if (status == null)
            return false;
        Matcher mat = urlPattern.matcher(status);
        boolean result = mat.find();
        mat.reset();
        return result;
    }

    public String extractURL() {
        Matcher mat = urlPattern.matcher(status);
        mat.find();
        return mat.group();
    }

    public int getWeight() {
        if (weight == -1) {
            weight = calculateWeight();
        }
        return weight;
    }

    private int calculateWeight() {
        int result = 0;
        boolean containUrl = hasLink();
        if (containUrl)
            result++;
        if (imageUrl != null)
            result++;
        if (content != null)
            result++;
        return result;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public URL getPortraitImageUrl() {
        return portraitImageUrl;
    }

    public void setPortraitImageUrl(URL portraitImageUrl) {
        this.portraitImageUrl = portraitImageUrl;
    }

    public String getTitle() {
        return title;
    }

    public int getTitleLength() {
        String str1 = title;
        int numberOfFull = 0;
        for (int i = 0; i < str1.length(); i++) {
            String test = str1.substring(i, i + 1);
            if (test.matches("[\\u4E00-\\u9FA5]+")) {
                numberOfFull += 2;
            } else {
                numberOfFull++;
            }

        }
        return numberOfFull;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return shorten(author);
    }

    private String shorten(String original) {
        if (original == null)
            return "";
        final String[] split = original.split("[_-]");
        if (split.length == 1)
            return original;
        return split[0];
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public URL getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(URL imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSourceType() {
        return sourceType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public void setImageBitmap(Bitmap image) {
        if (image == null)
            return;

        this.image = image;
        //Log.d("imageLoading","loaded");
        if (notifier != null && image != null)
            notifier.notifyImageLoaded();
    }

    public Bitmap getImage() {
        return image;
    }

    public void addNotifier(ExpandableArticleView.Notifier notifier) {
        this.notifier = notifier;
    }

    public void setAlreadyLoaded(boolean alreadyLoaded) {
        this.alreadyLoaded = alreadyLoaded;
    }

    private volatile boolean loading = false;

    public synchronized void loadPrimaryImage(String image,DeviceInfo deviceInfo) {
        PreloadPrimaryImageLoaderHandler preloadPrimaryImageLoaderHandler = new PreloadPrimaryImageLoaderHandler(this,image,deviceInfo);
        final ImageLoader loader = new ImageLoader(image, preloadPrimaryImageLoaderHandler);
        new Thread(loader).start();
    }

    public void loadPrimaryImage(DeviceInfo deviceInfo) {
        if(getImageUrl()!=null)
            loadPrimaryImage(getImageUrl().toExternalForm(),deviceInfo);
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public boolean isLoading() {
        return loading;
    }

    public void loadSecondaryImages(DeviceInfo deviceInfo) {
        for (String url : imagesMap.keySet()) {
            loadSecondaryImage(url,deviceInfo);
        }
    }

    public synchronized void loadSecondaryImage(String image,DeviceInfo deviceInfo) {
        PreloadSecondaryImageLoaderHandler preloadSecondaryImageLoaderHandler = new PreloadSecondaryImageLoaderHandler(this, image,deviceInfo);

        final ImageLoader loader = new ImageLoader(image, preloadSecondaryImageLoaderHandler);
        new Thread(loader).start();
    }

    public void onSecondaryImageLoaded(Bitmap bitmap, String url) {
        this.getImagesMap().put(url, bitmap);
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }
}
