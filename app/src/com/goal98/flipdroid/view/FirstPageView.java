package com.goal98.flipdroid.view;

import android.content.Context;
import android.content.res.AssetManager;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
public class FirstPageView extends WeiboPageView {
    private LinearLayout frame;
    private FromFileJSONReader jsonReader;

    public FirstPageView(PageActivity context, PageViewSlidingWindows windows,ExecutorService executor) {
        super(context);
        jsonReader = new FromFileJSONReader(context);
    }

    protected void setDynamicLayout(Context context) {
        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        this.frame = (LinearLayout) inflater.inflate(R.layout.first_page_view, null);
        TextView textView = (TextView) this.frame.findViewById(R.id.tips);

        TipsRepo tipsRepo = new TipsRepo(context);
        List<Tip> tips = tipsRepo.getTips();
        int size = tips.size();
        if (size > 0) {
            Random random = new Random();
            int tipId = random.nextInt(size);
            Tip tip = tips.get(tipId);
            textView.setText("小贴士:" + tip.getText());

            ImageView imageView = (ImageView) this.frame.findViewById(R.id.tipImage);
            imageView.setAlpha(130);
            try {
                Class clazz = Class.forName("com.goal98.flipdroid.R$drawable");
                int tipImage=clazz.getField("tips_" +tip.getId()).getInt(clazz);
                imageView.setImageResource(tipImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.addView(frame, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    }

    public void showLoading() {
//        LayoutInflater inflater = LayoutInflater.from(WeiboPageView.this.getContext());
//        LinearLayout loadingView = (LinearLayout) inflater.inflate(R.layout.loading, null);
//        this.addView(loadingView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
    }

    public boolean isFirstPage() {
        return true;  //To change body of created methods use File | Settings | File Templates.
    }

    public void releaseResource() {

    }
}
