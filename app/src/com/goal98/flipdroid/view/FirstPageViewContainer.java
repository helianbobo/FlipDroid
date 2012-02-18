package com.goal98.flipdroid.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.goal98.android.WebImageView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.activity.PageActivity;
import com.goal98.flipdroid.model.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 5/31/11
 * Time: 10:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class FirstPageViewContainer extends ThumbnailViewContainer {
    private RelativeLayout frame;
    private WebImageView imageView;

    //    private TipsRepo tipsRepo;
    public FirstPageViewContainer(PageActivity context) {
        super(context);

    }

    protected void setDynamicLayout(Context context) {
//        tipsRepo = new TipsRepo(context);
        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        this.frame = (RelativeLayout) inflater.inflate(R.layout.first_page_view_no_tip, null);
        TextView textView = (TextView) this.frame.findViewById(R.id.sourceName);
        textView.setText(sourceName);
        imageView = (WebImageView)this.frame.findViewById(R.id.portrait);
        imageView.setImageUrl(sourceImageURL);

//        List<Tip> tips = tipsRepo.getTips();
//        int size = tips.size();
//        if (size > 0) {
//            Random random = new Random();
//            int tipId = random.nextInt(size);
//            Tip tip = tips.get(tipId);
//            textView.setText("小贴士:" + tip.getText());
//
//            ImageView imageView = (ImageView) this.frame.findViewById(R.id.tipImage);
//            imageView.setAlpha(130);
//            try {
//                Class clazz = Class.forName("com.goal98.flipdroid.R$drawable");
//                int tipImage=clazz.getField("tips_" +tip.getId()).getInt(clazz);
//                imageView.setImageResource(tipImage);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        this.addView(frame, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    }

    public void showLoading() {
//        LayoutInflater inflater = LayoutInflater.from(ThumbnailViewContainer.this.getContext());
//        LinearLayout loadingView = (LinearLayout) inflater.inflate(R.layout.loading, null);
//        this.addView(loadingView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
    }

    public boolean isFirstPage() {
        return true;  //To change body of created methods use File | Settings | File Templates.
    }

    public void releaseResource() {

    }

    @Override
    public void renderBeforeLayout() {
        super.renderBeforeLayout();
        imageView.loadImage();
    }
}
