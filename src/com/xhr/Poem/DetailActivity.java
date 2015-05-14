package com.xhr.Poem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.open.SocialConstants;
import com.tencent.tauth.Tencent;
import com.xhr.Poem.model.PoemItem;
import com.xhr.Poem.qq.BaseUIListener;
import com.xhr.Poem.view.ContentFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Created by xhrong on 2015/5/11.
 */
public class DetailActivity extends FragmentActivity implements ContentFragment.IPlayer, ContentFragment.IShare {

    //   private static UMSocialService mController;

    private ViewPager mPager;
    private int position;
    private MediaPlayer mediaPlayer;
    private Tencent mTencent;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        Intent intent = getIntent();
        if (intent == null) finish();
        position = intent.getIntExtra("position", -1);
        mediaPlayer = new MediaPlayer();
        initViewPager();
        mTencent = Tencent.createInstance(AppConfig.QQ_APPID, this.getApplicationContext());
    }

    @Override
    protected void onPause() {//如果突然电话到来，停止播放音乐
        if (isPlaying()) {
            stop();
        }
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        release();
        super.onDestroy();
    }

    /*
* 初始化ViewPager
*/
    private void initViewPager() {
        mPager = (ViewPager) findViewById(R.id.viewpager);
        mPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), AppState.getCurrentPoems()));
        mPager.setCurrentItem(position);
        mPager.setOffscreenPageLimit(2);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                //翻页时，停止音频播放
                if (isPlaying()) {
                    stop();
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });//页面变化时的监听器
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mTencent.onActivityResult(requestCode, resultCode, data);
    }


    public void share() {

    }


    public String poemToBitmap(PoemItem poemItem) {

        int x = 10, y, space = 10;
        try {
            //prepare data
            String title = poemItem.getTitle();
            String author = poemItem.getAuthor();
            String content = poemItem.getContent();
            String[] ss;
            if (content.contains("<br>"))
                ss = content.split("<br>");
            else
                ss = content.split("\n");

            String longestStr = "";
            for (String s : ss) {
                if (s.length() > longestStr.length()) {
                    longestStr = s;
                }
            }
            if (title.length() > longestStr.length() * 2 / 3) {
                title = title.substring(0, longestStr.length() * 2 / 3) + "...";
            }

            //measure
            Paint paint = new Paint();
            paint.setTextSize(24f);
            paint.setFakeBoldText(true);
            paint.setAntiAlias(true);
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            int titleHeight = (int) (fontMetrics.bottom - fontMetrics.top) + space;
            int titleWidth = (int) paint.measureText(title);
            paint.setTextSize(20f);
            paint.setFakeBoldText(false);
            int authorHeight = (int) (fontMetrics.bottom - fontMetrics.top);
            int authorWidth = (int) paint.measureText(author);

            paint.setTextSize(24f);
            paint.setAntiAlias(true);
            int contentItemHeight = (int) (fontMetrics.bottom - fontMetrics.top) + space;

            int width = (int) paint.measureText(longestStr) + 20;
            int height = titleHeight + authorHeight + contentItemHeight * ss.length;

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.WHITE);

            paint.setAntiAlias(true);
            //draw title
            y = titleHeight - 3 * space / 2;
            paint.setTextSize(24f);
            paint.setFakeBoldText(true);
            canvas.drawText(title, (width - titleWidth) / 2, y, paint);

            //draw author
            y += authorHeight;
            paint.setTextSize(20f);
            paint.setFakeBoldText(false);
            canvas.drawText(author, (width - authorWidth) / 2, y, paint);

            //draw content
            paint.setTextSize(24f);
            y += contentItemHeight;
            for (String s : ss) {
                canvas.drawText(s, x, y, paint);
                y = y + contentItemHeight;
            }


            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
            String path = AppConfig.TEMP_PATH + "/" + AppConfig.TEMP_IMAGE_NAME;
            FileOutputStream os = new FileOutputStream(new File(path));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, os);
            os.flush();
            os.close();
            return path;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }


    @Override
    public void play(String fileName) {
        try {
            File audioFile = new File(fileName);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(audioFile.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();//播放
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    @Override
    public void release() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }
    }

    @Override
    public boolean isPlaying() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            return true;
        }
        return false;
    }

    @Override
    public void share(PoemItem poemItem) {
        String imagePath = poemToBitmap(poemItem);
        Bundle bundle = new Bundle();
        bundle.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE);
        bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        //这条分享消息被好友点击后的跳转URL。
        bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, AppConfig.ShARE_TARGET_URL);
        //分享的标题。注：PARAM_TITLE、PARAM_IMAGE_URL、PARAM_	SUMMARY不能全为空，最少必须有一个是有值的。
        bundle.putString(SocialConstants.PARAM_TITLE, poemItem.getTitle() + " " + poemItem.getAudioUrl());
        //分享的图片URL
        bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imagePath);
        bundle.putString(QzoneShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imagePath);
        //   bundle.putString(SocialConstants.PARAM_IMAGE_URL,imagePath);
        // 分享的消息摘要，最长50个字
        //     bundle.putString(SocialConstants.PARAM_SUMMARY, "测试");
        // 手Q客户端顶部，替换“返回”按钮文字，如果为空，用返回代替
        bundle.putString(SocialConstants.PARAM_APPNAME, "唐诗三百首");
        // 标识该消息的来源应用，值为应用名称+AppId。
        bundle.putString(SocialConstants.PARAM_APP_SOURCE, "唐诗三百首" + AppConfig.QQ_APPID);
        mTencent.shareToQQ(this, bundle, new BaseUIListener(DetailActivity.this));
    }

    public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

        List<PoemItem> list;

        public MyFragmentPagerAdapter(FragmentManager fm, List<PoemItem> list) {
            super(fm);
            this.list = list;

        }

        @Override
        public void destroyItem(android.view.ViewGroup container, int position, java.lang.Object object) {

            super.destroyItem(container, position, object);

            Log.i("INFO", "Destroy Item...");
        }

        @Override
        public int getCount() {
            return list.size();
        }


        @Override
        public Fragment getItem(int position) {
            return ContentFragment.newInstance(list.get(position));
        }
    }
}
