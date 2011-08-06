package com.goal98.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.ViewSwitcher;

public class WebImageView extends ViewSwitcher {

    private String imageUrl;

    private boolean isLoaded;

    private ProgressBar loadingSpinner;

    public ImageView imageView;

    private ScaleType scaleType = ScaleType.CENTER_INSIDE;

    private Drawable progressDrawable, errorDrawable;

    private static final String ANDROID_XMLNS = "http://schemas.android.com/apk/res/android";
    private int width;
    private int height;
    private int defaultWidth;
    private int defaultHeight;


    /**
     * @param context  the view's current context
     * @param imageUrl the URL of the image to read and show
     * @param autoLoad Whether the read should start immediately after creating the view. If set to
     *                 false, use {@link #loadImage()} to manually trigger the image read.
     */
    public WebImageView(Context context, String imageUrl, boolean autoLoad) {
        super(context);
        initialize(context, imageUrl, null, null, autoLoad);
    }

    /**
     * @param context          the view's current context
     * @param imageUrl         the URL of the image to read and show
     * @param progressDrawable the drawable to be used for the {@link android.widget.ProgressBar} which is displayed while the
     *                         image is loading
     * @param autoLoad         Whether the read should start immediately after creating the view. If set to
     *                         false, use {@link #loadImage()} to manually trigger the image read.
     */
    public WebImageView(Context context, String imageUrl, Drawable progressDrawable,
                        boolean autoLoad) {
        super(context);
        initialize(context, imageUrl, progressDrawable, null, autoLoad);
    }

    /**
     * @param context          the view's current context
     * @param imageUrl         the URL of the image to read and show
     * @param progressDrawable the drawable to be used for the {@link android.widget.ProgressBar} which is displayed while the
     *                         image is loading
     * @param errorDrawable    the drawable to be used if a read error occurs
     * @param autoLoad         Whether the read should start immediately after creating the view. If set to
     *                         false, use {@link #loadImage()} to manually trigger the image read.
     */
    public WebImageView(Context context, String imageUrl, Drawable progressDrawable,
                        Drawable errorDrawable, boolean autoLoad) {
        super(context);
        initialize(context, imageUrl, progressDrawable, errorDrawable, autoLoad);
    }

    public WebImageView(Context context, AttributeSet attributes) {
        super(context, attributes);
        // TypedArray styles = context.obtainStyledAttributes(attributes,
        // R.styleable.GalleryItem);

        final String XMLNS = "http://schemas.android.com/apk/res/" + context.getPackageName();
        int progressDrawableId = attributes.getAttributeResourceValue(XMLNS,
                "progressDrawable", 0);
        int errorDrawableId = attributes.getAttributeResourceValue(XMLNS, "errorDrawable",
                0);

        defaultWidth = attributes.getAttributeIntValue(XMLNS, "defaultWidth",
                0);

        defaultHeight = attributes.getAttributeIntValue(XMLNS, "defaultHeight",
                0);

        Drawable progressDrawable = null;
        if (progressDrawableId > 0) {
            progressDrawable = context.getResources().getDrawable(progressDrawableId);
        }
        Drawable errorDrawable = null;
        if (errorDrawableId > 0) {
            errorDrawable = context.getResources().getDrawable(errorDrawableId);
        }
        initialize(context, attributes.getAttributeValue(XMLNS, "imageUrl"),
                progressDrawable, errorDrawable, attributes.getAttributeBooleanValue(
                XMLNS, "autoLoad",
                true));
        // styles.recycle();
    }

    public void setDefaultHeight(int defaultHeight) {
        this.defaultHeight = defaultHeight;
    }

    public void setDefaultWidth(int defaultWidth) {
        this.defaultWidth = defaultWidth;
    }

    private void initialize(Context context, String imageUrl, Drawable progressDrawable,
                            Drawable errorDrawable,
                            boolean autoLoad) {
        this.imageUrl = imageUrl;
        this.progressDrawable = progressDrawable;
        this.errorDrawable = errorDrawable;

        ImageLoader.initialize(context);

        // ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
        // 125.0f, preferredItemHeight / 2.0f);
        // anim.setDuration(500L);

        // AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        // anim.setDuration(500L);
        // setInAnimation(anim);

        addLoadingSpinnerView(context);
        addImageView(context);

        if (autoLoad && imageUrl != null) {
            loadImage();
        }
    }

    private void addLoadingSpinnerView(Context context) {
        loadingSpinner = new ProgressBar(context);
        loadingSpinner.setIndeterminate(true);
        if (this.progressDrawable == null) {
            this.progressDrawable = loadingSpinner.getIndeterminateDrawable();
        } else {
            loadingSpinner.setIndeterminateDrawable(progressDrawable);
            if (progressDrawable instanceof AnimationDrawable) {
                ((AnimationDrawable) progressDrawable).start();
            }
        }

        LayoutParams lp = new LayoutParams(progressDrawable.getIntrinsicWidth(), progressDrawable
                .getIntrinsicHeight());
        lp.gravity = Gravity.CENTER;

        addView(loadingSpinner, 0, lp);
    }

    private void addImageView(Context context) {
        imageView = new ImageView(context);
        imageView.setScaleType(scaleType);
        imageView.setAdjustViewBounds(true);

        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        addView(imageView, 1, lp);
    }

    /**
     * Use this method to trigger the image read if you had previously set autoLoad to false.
     */
    public void loadImage() {
        if (imageUrl == null) {
            return;
        }
        if (imageUrl.length() == 0)
            return;
        handler = new DefaultImageLoaderHandler();
        ImageLoader.start(imageUrl, handler);
    }

    public DefaultImageLoaderHandler handler;

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Often you have resources which usually have an image, but some don't. For these cases, use
     * this method to supply a placeholder drawable which will be loaded instead of a web image.
     *
     * @param imageResourceId the resource of the placeholder image drawable
     */
    public void setNoImageDrawable(int imageResourceId) {
        imageView.setImageDrawable(getContext().getResources().getDrawable(imageResourceId));
        setDisplayedChild(1);
    }

    @Override
    public void reset() {
        super.reset();

        this.setDisplayedChild(0);
    }

    public void handleImageLoaded(Bitmap image, Object o) {
        if (handler == null)
            handler = new DefaultImageLoaderHandler();

        handler.handleImageLoaded(image, null);
    }

    int fatOrSlim = 0;

    public int getFatOrSlim() {
        return fatOrSlim;
    }

    public void setFatOrSlim(int fatOrSlim) {
        this.fatOrSlim = fatOrSlim;
    }

    public static final int FAT = 0;
    public static final int SLIM = 1;

    private int percentageInWidth = 0;

    public int getPercentageInWidth() {
        return percentageInWidth;
    }

    public void setPercentageInWidth(int percentageInWidth) {
        this.percentageInWidth = percentageInWidth;
    }

    private class DefaultImageLoaderHandler extends ImageLoaderHandler {

        public DefaultImageLoaderHandler() {
            super(imageView, imageUrl, errorDrawable);
        }

        @Override
        public boolean handleImageLoaded(Bitmap bitmap, Message msg) {

            if (bitmap == null)
                return false;

            int bmpWidth = bitmap.getWidth();

            int bmpHeight = bitmap.getHeight();

            System.out.println("imageSize" + bmpWidth + "," + bmpHeight);
            //缩放图片的尺寸

            int width = WebImageView.this.getWidth() == 0 ? WebImageView.this.defaultWidth : WebImageView.this.getWidth();
            int height = WebImageView.this.getHeight() == 0 ? WebImageView.this.defaultHeight : WebImageView.this.getHeight();
//            int height = width * bmpHeight / bmpWidth;

            int heightDip = 160 * bmpHeight / DisplayMetrics.DENSITY_DEFAULT;
            int widthDip = 160 * bmpWidth / DisplayMetrics.DENSITY_DEFAULT;

            System.out.println("bmpWidth" + bmpWidth);
            System.out.println("bmpHeight" + bmpHeight);
            System.out.println("width" + width);
            System.out.println("height" + height);

            float scale = 0.0f;
            if (bmpWidth > bmpHeight * 1.15) {
                scale = (float) width / widthDip;
                fatOrSlim = FAT;
            } else {
                scale = (float) height / heightDip;
                fatOrSlim = SLIM;
                percentageInWidth = 50 * (bmpWidth / bmpHeight);
            }
            System.out.println("scale" + scale);

            Bitmap resizeBitmap = null;
            if (scale != 1.0) {
                Matrix matrix = new Matrix();

                matrix.postScale(scale, scale);


                //产生缩放后的Bitmap对象


                try {
                    resizeBitmap = Bitmap.createBitmap(
                            bitmap, 0, 0, bmpWidth, bmpHeight, matrix, false);

                    if (preloadImageLoaderHandler != null)
                        preloadImageLoaderHandler.onImageResized(resizeBitmap, imageUrl);
                    bitmap.recycle();
                } catch (Throwable error) {
                    error.printStackTrace();
                    System.out.println("out of memory...skipped");
                }
            }else {
                 resizeBitmap = bitmap;
            }
            boolean result = false;
            String forUrl = (String) imageView.getTag();
            if (imageUrl.equals(forUrl)) {
                resizeBitmap = resizeBitmap != null || errorDrawable == null ? resizeBitmap
                        : ((BitmapDrawable) errorDrawable).getBitmap();

                if (resizeBitmap != null) {
                    imageView.setImageBitmap(resizeBitmap);
                }

                result = true;
            } else {
                result = false;
            }

            System.out.println("RESULT" + result);
            if (result) {
                isLoaded = true;
                setDisplayedChild(1);
            }

            return result;
        }
    }

    /**
     * Returns the URL of the image to show
     *
     * @return
     */
    public String getImageUrl() {
        return imageUrl;
    }
}
