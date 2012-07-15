package com.goal98.flipdroid2.model;

import android.graphics.Bitmap;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import com.srz.androidtools.util.DeviceInfo;
import com.goal98.flipdroid2.util.TextPaintUtil;
import com.goal98.flipdroid2.view.Notifier;
import com.goal98.tika.common.Paragraphs;
import com.goal98.tika.common.TikaUIObject;

import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Article implements Comparable {
    private boolean favorite;

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
    //    private Bitmap image;
    private Notifier notifier;
    private int height;
    private Map<String, Bitmap> imagesMap = new HashMap<String, Bitmap>();
    private List<String> images = new ArrayList<String>();
    private int imageWidth;
    private int imageHeight;
    private boolean layoutVertical;
    private float suggestScale;
    private int textHeight;
    private String sourceURL;
    private String from;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSourceURL() {
        return sourceURL;
    }

    public void setSourceURL(String sourceURL) {
        this.sourceURL = sourceURL;
    }

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

//    public String extractLink() {
//        if (status == null)
//            return null;
//        Matcher mat = urlPattern.matcher(status);
//        boolean result = mat.find();
//        if(result){
//            return mat.group();
//        }
//       return null;
//    }

    public String extractURL() {
        if (status != null) {
            Matcher mat = urlPattern.matcher(status);
            if (mat.find())
                return mat.group();
            return null;
        } else {
            return null;
        }
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
        thumbnailText = Html.fromHtml(content.replaceAll("(<br/>)|(</h[1-6]+>)|(<h[1-6]+>)|(<img.*?>)|(<blockquote>)|(</blockquote>)|(hack</img>)", ""));

        Spannable spannable = Spannable.Factory.getInstance().newSpannable(thumbnailText);

        TextPaintUtil.removeUnderlines(spannable);
        thumbnailText = spannable;
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
        expandable = true;
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

//    public void setImageBitmap(Bitmap image) {
//        if(this.image!=null){
//            this.image.recycle();
//        }
//
////        Iterator<String> itor = this.imagesMap.keySet().iterator();
////        while(itor.hasNext()){
////            String key = itor.next();
////            if(imagesMap.get(key).equals(this.image)){
////                im
////            }
////        }
//        this.image = image;
//        //Log.d("imageLoading","loaded");
//        if (notifier != null && image != null)
//            notifier.notifyImageLoaded();
//    }

//    public Bitmap getImage() {
//        return image;
//    }

    public void addNotifier(Notifier notifier) {
        this.notifier = notifier;
    }

    public void setAlreadyLoaded(boolean alreadyLoaded) {
        this.alreadyLoaded = alreadyLoaded;
    }

    private volatile boolean loading = false;

//    public synchronized void loadPrimaryImage(String image, DeviceInfo deviceInfo, boolean loadFromInternet) {
//        PreloadPrimaryImageLoaderHandler preloadPrimaryImageLoaderHandler = new PreloadPrimaryImageLoaderHandler(this, image, deviceInfo);
//        final ImageLoader loader = new ImageLoader(image, preloadPrimaryImageLoaderHandler, loadFromInternet);
//        new Thread(loader).start();
//    }

//    public void loadPrimaryImage(DeviceInfo deviceInfo, boolean loadFromInternet) {
//        if (getImageUrl() != null)
//            loadPrimaryImage(getImageUrl().toExternalForm(), deviceInfo, loadFromInternet);
//    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public boolean isLoading() {
        return loading;
    }

//    public void loadSecondaryImages(DeviceInfo deviceInfo) {
//        for (String url : imagesMap.keySet()) {
//            loadSecondaryImage(url, deviceInfo);
//        }
//    }

//    public synchronized void loadSecondaryImage(String image, DeviceInfo deviceInfo, boolean loadFromInternet) {
//        PreloadSecondaryImageLoaderHandler preloadSecondaryImageLoaderHandler = new PreloadSecondaryImageLoaderHandler(this, image, deviceInfo);
//
//        final ImageLoader loader = new ImageLoader(image, preloadSecondaryImageLoaderHandler, loadFromInternet);
//        new Thread(loader).start();
//    }

//    public void onSecondaryImageLoaded(Bitmap bitmap, String url) {
//        this.getImagesMap().put(url, bitmap);
//    }

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

    public String getPreviewParagraph() {
        String paragraph1 = "";
        Paragraphs paragraphs = new Paragraphs();
        paragraphs.toParagraph(getContent());

        if (paragraphs.getParagraphs() != null && paragraphs.getParagraphs().size() != 0) {
            for (int i = 0; i < paragraphs.getParagraphs().size(); i++) {
                TikaUIObject uiObject = paragraphs.getParagraphs().get(i);
                if (!uiObject.getType().equals(TikaUIObject.TYPE_TEXT))
                    continue;

                paragraph1 = uiObject.getObjectBody().replaceAll("\\<[/]?.+?\\>", "");
                if (paragraph1.length() < 40)
                    continue;
                else
                    break;
            }
        }
        return paragraph1;
    }

    public String getToParagraph(DeviceInfo deviceInfo) {
        if (expandable) {
            return content;
        } else {
            if (getImageUrl() != null && getImageWidth() != 0 && getImageHeight() != 0) {
                return "<p>" + content + "</p>" + "<img src=" + getImageUrl() + " width=" + getImageWidth() + " height=" + getImageHeight() + " >hack</img>";
            } else
                return "<p>" + content + "</p>";
        }


    }

    public int compareTo(Object o) {
        Article a = (Article) o;
        if (a.getCreatedDate().before(createdDate))
            return -1;
        return 1;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private Spanned thumbnailText;

    public Spanned getThumbnailText() {
        return thumbnailText;
    }


    public boolean isFavorite() {
        return favorite;
    }

    public void setIsFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Article article = (Article) o;

        if (content != null ? !content.equals(article.content) : article.content != null) return false;
        if (title != null ? !title.equals(article.title) : article.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (content != null ? content.hashCode() : 0);
        return result;
    }
}
