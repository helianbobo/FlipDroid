package com.goal98.android;

import android.R;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
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


    private ScaleType scaleType = ScaleType.FIT_CENTER;

    private Drawable progressDrawable, errorDrawable;

    private String TAG = this.getClass().getName();

    private boolean autoSize = false;
    private OnImageLoadedListener onImageloadedListener;

    public void setAutoSize(boolean autoSize) {
        this.autoSize = autoSize;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    private static final String ANDROID_XMLNS = "http://schemas.android.com/apk/res/android";
    private int width;
    private int height;
    private int defaultWidth;
    private int defaultHeight;
    private boolean roundImage;
    private boolean loadFromInternetFlag = true;

    public boolean isRoundImage() {
        return roundImage;
    }

    public void setRoundImage(boolean roundImage) {
        this.roundImage = roundImage;
    }

    /**
     * @param context  the view's current context
     * @param imageUrl the URL of the it.tika.mongodb.image to read and show
     * @param autoLoad Whether the read should start immediately after creating the view. If set to
     *                 false, use {@link #loadImage()} to manually trigger the it.tika.mongodb.image read.
     */
    public WebImageView(Context context, String imageUrl, boolean autoLoad, boolean loadFromInternetFlag) {
        super(context);
        this.loadFromInternetFlag = loadFromInternetFlag;
        initialize(context, imageUrl, null, null, autoLoad);
    }

    /**
     * @param context          the view's current context
     * @param imageUrl         the URL of the it.tika.mongodb.image to read and show
     * @param progressDrawable the drawable to be used for the {@link android.widget.ProgressBar} which is displayed while the
     *                         it.tika.mongodb.image is loading
     * @param autoLoad         Whether the read should start immediately after creating the view. If set to
     *                         false, use {@link #loadImage()} to manually trigger the it.tika.mongodb.image read.
     */
    public WebImageView(Context context, String imageUrl, Drawable progressDrawable,
                        boolean autoLoad) {
        super(context);
        initialize(context, imageUrl, progressDrawable, null, autoLoad);
    }

    /**
     * @param context          the view's current context
     * @param imageUrl         the URL of the it.tika.mongodb.image to read and show
     * @param progressDrawable the drawable to be used for the {@link android.widget.ProgressBar} which is displayed while the
     *                         it.tika.mongodb.image is loading
     * @param errorDrawable    the drawable to be used if a read error occurs
     * @param autoLoad         Whether the read should start immediately after creating the view. If set to
     *                         false, use {@link #loadImage()} to manually trigger the it.tika.mongodb.image read.
     */
    public WebImageView(Context context, String imageUrl, Drawable progressDrawable,
                        Drawable errorDrawable, boolean autoLoad, boolean loadFromInternetFlag, ScaleType scaleType) {
        super(context);
        this.loadFromInternetFlag = loadFromInternetFlag;
        this.scaleType = scaleType;
        initialize(context, imageUrl, progressDrawable, errorDrawable, autoLoad);
    }

    public WebImageView(Context context, String imageUrl, Drawable progressDrawable,
                        Drawable errorDrawable, boolean autoLoad, boolean roundImage, boolean loadFromInternetFlag, ScaleType scaleType) {
        super(context);
        this.roundImage = roundImage;
        this.loadFromInternetFlag = loadFromInternetFlag;
        this.scaleType = scaleType;
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

        roundImage = attributes.getAttributeBooleanValue(XMLNS, "roundImage", false);

        String scaleType = attributes.getAttributeValue(XMLNS, "scaleType");
        System.out.println("scale type" + scaleType);
        if (scaleType != null)
            this.scaleType = ScaleType.valueOf(scaleType);

        loadFromInternetFlag = attributes.getAttributeBooleanValue(XMLNS, "loadFromInternet", true);

        Drawable progressDrawable = null;
        if (progressDrawableId > 0) {
            progressDrawable = context.getResources().getDrawable(progressDrawableId);
        }
        Drawable errorDrawable = null;
        if (errorDrawableId > 0) {
            errorDrawable = context.getResources().getDrawable(errorDrawableId);
        }
        initialize(context,
                attributes.getAttributeValue(XMLNS, "imageUrl"),
                progressDrawable,
                errorDrawable,
                attributes.getAttributeBooleanValue(XMLNS, "autoLoad", true));
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
        pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        this.imageUrl = imageUrl;
        this.progressDrawable = progressDrawable;
        this.errorDrawable = errorDrawable;
        setBackgroundColor(Color.TRANSPARENT);
        this.setInAnimation(this.getContext(), R.anim.fade_in);
        ImageLoader.initialize(context);

        // ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
        // 125.0f, preferredItemHeight / 2.0f);
        // anim.setDuration(500L);

        // AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        // anim.setDuration(500L);
        // setInAnimation(anim);
//        setBackgroundColor(Color.parseColor("#FF343434"));
        addLoadingSpinnerView(context);
        addImageView(context);

        if (autoLoad && imageUrl != null) {
            loadImage();
        }
    }

    private void addLoadingSpinnerView(Context context) {
        loadingSpinner = new ProgressBar(context);
        loadingSpinner.setIndeterminate(true);

        LayoutParams lp = null;

        if (this.progressDrawable == null) {
            this.progressDrawable = loadingSpinner.getIndeterminateDrawable();
            lp = new LayoutParams(progressDrawable.getIntrinsicWidth() / 2, progressDrawable
                    .getIntrinsicHeight() / 2);
        } else {
            loadingSpinner.setIndeterminateDrawable(progressDrawable);
            loadingSpinner.setPadding(15, 15, 15, 15);
            if (progressDrawable instanceof AnimationDrawable) {
                ((AnimationDrawable) progressDrawable).start();
            }
            lp = new LayoutParams(progressDrawable.getIntrinsicWidth(), progressDrawable
                    .getIntrinsicHeight());
        }


        lp.gravity = Gravity.CENTER;

        addView(loadingSpinner, 0, lp);
    }

    private void addImageView(Context context) {
        imageView = new ImageView(context);
        imageView.setScaleType(scaleType);
        imageView.setAdjustViewBounds(false);

        LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        lp.gravity = Gravity.CENTER;
        addView(imageView, 1, lp);
    }

    /**
     * Use this method to trigger the it.tika.mongodb.image read if you had previously set autoLoad to false.
     */
    public void loadImage() {
        if (imageUrl == null) {
            return;
        }
        if (imageUrl.length() == 0)
            return;
        handler = new DefaultImageLoaderHandler(this);
        handler.setOnImageLoadedListener(onImageloadedListener);
        ImageLoader.start(imageUrl, handler, loadFromInternetFlag);

//        final String cacheDir = this.getContext().getCacheDir().getAbsolutePath();
//        if (ResponseCache.getDefault() == null)
//            ResponseCache.setDefault(new ResponseCache() {
//                @Override
//                public CacheResponse get(URI uri, String s, Map<String, List<String>> headers) throws IOException {
//                    final File file = new File(cacheDir, escape(uri.getPath()));
//                    if (file.exists()) {
//                        return new CacheResponse() {
//                            @Override
//                            public Map<String, List<String>> getHeaders() throws IOException {
//                                return null;
//                            }
//
//                            @Override
//                            public InputStream getBody() throws IOException {
//                                return new FileInputStream(file);
//                            }
//                        };
//                    } else {
//                        return null;
//                    }
//                }
//
//                @Override
//                public CacheRequest put(URI uri, URLConnection urlConnection) throws IOException {
//                    final File file = new File(cacheDir, escape(urlConnection.getURL().getPath()));
//                    return new CacheRequest() {
//                        @Override
//                        public OutputStream getBody() throws IOException {
//                            return new FileOutputStream(file);
//                        }
//
//                        @Override
//                        public void abort() {
//                            file.delete();
//                        }
//                    };
//                }
//
//                private String escape(String url) {
//                    return url.replace("/", "-").replace(".", "-");
//                }
//            });
//
//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                URL url = null;
//                try {
//                    url = new URL(imageUrl);
//                    URLConnection connection = url.openConnection();
//                    connection.setUseCaches(true);
//                    final Drawable drawable = Drawable.createFromStream(connection.getInputStream(), "src");
//
//                    BitmapDrawable bd = (BitmapDrawable) drawable;
//                    if (bd == null)
//                        return;
//
//                    final Bitmap bm = bd.getBitmap();
//
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            imageView.setImageBitmap(bm);
//                            setDisplayedChild(1);
//                        }
//                    });
//                } catch (IOException e) {
//                }
//            }
//        });
//        t.start();
    }


    public DefaultImageLoaderHandler handler;

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Often you have resources which usually have an it.tika.mongodb.image, but some don't. For these cases, use
     * this method to supply a placeholder drawable which will be loaded instead of a web it.tika.mongodb.image.
     *
     * @param imageResourceId the resource of the placeholder it.tika.mongodb.image drawable
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
        if (onImageloadedListener != null)
            onImageloadedListener.onLoaded(imageView);
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

    public void setOnImageloaded(OnImageLoadedListener onImageLoadedListener) {
        this.onImageloadedListener = onImageLoadedListener;
    }

    private class DefaultImageLoaderHandler extends ImageLoaderHandler {

        private WebImageView webImageView;

        public DefaultImageLoaderHandler() {
            super(imageView, imageUrl, errorDrawable);
        }

        public DefaultImageLoaderHandler(WebImageView webImageView) {
            this();
            this.webImageView = webImageView;
        }

        @Override
        public boolean handleImageLoaded(Bitmap bitmap, Message msg) {

            if (bitmap == null)
                return false;

//            try {
//                if (false) //comment it out till black issue solved
//                    bitmap = ImageHelper.getRoundedCornerBitmap(bitmap, 3);
//            } catch (Throwable e) {
//                Log.w(this.getClass().getName(), "Failed to round image.", e);
//            }

            int bmpWidth = bitmap.getWidth();

            int bmpHeight = bitmap.getHeight();

            Log.v(TAG, "imageSize" + bmpWidth + "," + bmpHeight);
            //缩放图片的尺寸

            int width = 0;

            if (autoSize) {
                width = bmpWidth;
            } else {
                width = WebImageView.this.getWidth() == 0 ? WebImageView.this.defaultWidth : WebImageView.this.getWidth();
            }

            float scale = 0.0f;
            scale = (float) width / bmpWidth;

            if (scale > 1) {
                scale = 1;
            }

            Bitmap resizeBitmap = null;
//            if (scale <= 1 && scale > 0.5) {
//                scale = 1.0f;
//            }
//            if (scale <= 0.5 && scale > 0.25) {
//                scale = 0.5f;
//            }
//            if (scale <= 0.25 && scale > 0.125) {
//                scale = 0.25f;
//            }
//            if(scale == 0)
//                scale = 1;

            System.out.println("jleo scale:" + scale);
            if (scale != 1.0) {
                resizeBitmap = resizeBitmap(bitmap, scale);
            } else {
                resizeBitmap = bitmap;
            }

            boolean result = false;
            String forUrl = (String) imageView.getTag();
            if (imageUrl.equals(forUrl)) {
                resizeBitmap = resizeBitmap != null || errorDrawable == null ? resizeBitmap
                        : ((BitmapDrawable) errorDrawable).getBitmap();

                if (resizeBitmap != null) {
//                    BitmapDrawable bd= new BitmapDrawable(resizeBitmap);
//                    bd.setAntiAlias(true);
//                    imageView.setImageDrawable(bd);
                    imageView.setImageBitmap(resizeBitmap);
                    ((BitmapDrawable) imageView.getDrawable()).setAntiAlias(true);
//                    imageView.
//                    imageView.invalidate();
                }

                result = true;
            } else {
                result = false;
            }
//            LinearLayout.LayoutParams newParams = new LinearLayout.LayoutParams(width, height);
//            if(WebImageView.this.getLayoutParams()!=null)
//                newParams.gravity = ((LinearLayout.LayoutParams) WebImageView.this.getLayoutParams()).gravity;
//            WebImageView.this.setLayoutParams(newParams);
            if (result) {
                isLoaded = true;

                setDisplayedChild(1);
            }
            if (webImageView.onImageloadedListener != null)
                webImageView.onImageloadedListener.onLoaded(webImageView.getImageView());
            
            return result;
        }

        private Bitmap resizeBitmap(Bitmap bitmap, float scale) {
            Matrix matrix = new Matrix();

            matrix.postScale(scale, scale);


            //产生缩放后的Bitmap对象
            Bitmap resizeBitmap = null;

            try {
//                resizeBitmap = Bitmap.createBitmap(
//                        bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
                resizeBitmap = createScaledBitmap(bitmap, (int) (bitmap.getWidth() * scale), (int) (bitmap.getHeight() * scale), ScalingLogic.FIT);
                //Bitmap.createScaledBitmap(bitmap, (int)(bitmap.getWidth()*scale),(int)(bitmap.getHeight()*scale),true);
                bitmap = null;
//                System.gc();
            } catch (Throwable error) {
                Log.e(TAG, "out of memory...skipped", error);
            }
            return resizeBitmap;
        }

        public Bitmap createScaledBitmap(Bitmap unscaledBitmap, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {

            Rect srcRect = calculateSrcRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth, dstHeight, scalingLogic);

            Rect dstRect = calculateDstRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth, dstHeight, scalingLogic);

            Bitmap scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(), Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(scaledBitmap);

            canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, new Paint(Paint.FILTER_BITMAP_FLAG));
            unscaledBitmap.recycle();
            return scaledBitmap;

        }

        public Rect calculateSrcRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {

            if (scalingLogic == ScalingLogic.CROP) {

                final float srcAspect = (float) srcWidth / (float) srcHeight;

                final float dstAspect = (float) dstWidth / (float) dstHeight;

                if (srcAspect > dstAspect) {

                    final int srcRectWidth = (int) (srcHeight * dstAspect);

                    final int srcRectLeft = (srcWidth - srcRectWidth) / 2;

                    return new Rect(srcRectLeft, 0, srcRectLeft + srcRectWidth, srcHeight);

                } else {

                    final int srcRectHeight = (int) (srcWidth / dstAspect);

                    final int scrRectTop = (int) (srcHeight - srcRectHeight) / 2;

                    return new Rect(0, scrRectTop, srcWidth, scrRectTop + srcRectHeight);

                }

            } else {

                return new Rect(0, 0, srcWidth, srcHeight);

            }

        }

        public Rect calculateDstRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {

            if (scalingLogic == ScalingLogic.FIT) {

                final float srcAspect = (float) srcWidth / (float) srcHeight;

                final float dstAspect = (float) dstWidth / (float) dstHeight;

                if (srcAspect > dstAspect) {

                    return new Rect(0, 0, dstWidth, (int) (dstWidth / srcAspect));

                } else {

                    return new Rect(0, 0, (int) (dstHeight * srcAspect), dstHeight);

                }

            } else {

                return new Rect(0, 0, dstWidth, dstHeight);

            }

        }
    }

    /**
     * Returns the URL of the it.tika.mongodb.image to show
     *
     * @return
     */
    public String getImageUrl() {
        return imageUrl;
    }

    private PaintFlagsDrawFilter pfd;

    protected void dispatchDraw(android.graphics.Canvas canvas) {
        canvas.setDrawFilter(pfd);
        try {
            super.dispatchDraw(canvas);
        } catch (Exception e) {
            int i = 0;
            i++;
        }
    }

    public interface OnImageLoadedListener {
        public void onLoaded(ImageView imageView1);
    }
}
