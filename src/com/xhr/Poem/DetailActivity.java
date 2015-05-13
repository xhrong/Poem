package com.xhr.Poem;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMEvernoteHandler;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.media.EvernoteShareContent;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.xhr.Poem.model.PoemItem;
import com.xhr.Poem.view.ContentFragment;

import java.io.File;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by xhrong on 2015/5/11.
 */
public class DetailActivity extends FragmentActivity implements ContentFragment.IPlayer ,ContentFragment.IShare{

    private final static String appDownloadPage = "http://xhrong.xicp.net:14468/";
    private static UMSocialService mController;

    private ViewPager mPager;
    private int position;
    private  MediaPlayer mediaPlayer;




    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        Intent intent = getIntent();
        if (intent == null) finish();
        position = intent.getIntExtra("position", -1);
        mediaPlayer = new MediaPlayer();
        initViewPager();
        // 配置需要分享的相关平台
        configPlatforms();
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
        setShareContent(poemItem);
        mController.openShare(DetailActivity.this, false);
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



    /**
     * 根据不同的平台设置不同的分享内容</br>
     */
    private void setShareContent(PoemItem poemItem) {
        mController = UMServiceFactory.getUMSocialService("com.umeng.share");
        mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.EVERNOTE);
        UMImage localImage = new UMImage(DetailActivity.this, R.drawable.icon);

        // 设置微信朋友圈分享的内容
        CircleShareContent circleShareContent = new CircleShareContent();
        circleShareContent.setShareContent(poemItem.getContent().replace("<br>", "\r\n"));
        circleShareContent.setTitle(poemItem.getTitle() + "  " + poemItem.getAuthor());
        circleShareContent.setShareImage(localImage);
        circleShareContent.setTargetUrl(appDownloadPage);
        mController.setShareMedia(circleShareContent);

        //设置微信分享内容
        WeiXinShareContent weiXinShareContent = new WeiXinShareContent();
        weiXinShareContent.setShareContent(poemItem.getContent().replace("<br>", "\r\n"));
        weiXinShareContent.setTitle(poemItem.getTitle() + "  " + poemItem.getAuthor());
        weiXinShareContent.setTargetUrl(appDownloadPage);
        weiXinShareContent.setShareMedia(localImage);
        mController.setShareMedia(weiXinShareContent);

        // 设置QQ空间分享内容
        QZoneShareContent qZoneShareContent = new QZoneShareContent();
        qZoneShareContent.setShareContent(poemItem.getContent().replace("<br>", "\r\n"));
        qZoneShareContent.setShareImage(localImage);
        qZoneShareContent.setTargetUrl(appDownloadPage);
        qZoneShareContent.setTitle(poemItem.getTitle() + "  " + poemItem.getAuthor());
        mController.setShareMedia(qZoneShareContent);

        //设置QQ分享内容
        QQShareContent qqShareContent = new QQShareContent();
        qqShareContent.setShareContent(poemItem.getContent().replace("<br>", "\r\n"));
        qqShareContent.setShareImage(localImage);
        qqShareContent.setTitle(poemItem.getTitle() + "  " + poemItem.getAuthor());
        String targetUrl = appDownloadPage + "?title=" + toURLEncoded(poemItem.getTitle())
                + "&author=" + toURLEncoded(poemItem.getAuthor())
                + "&content=" + toURLEncoded(poemItem.getContent());
        qqShareContent.setTargetUrl(targetUrl);
        mController.setShareMedia(qqShareContent);

        // 设置evernote的分享内容
        EvernoteShareContent evernoteShareContent = new EvernoteShareContent(poemItem.getContent().replace("<br>", "\r\n"));
        evernoteShareContent.setTitle(poemItem.getTitle() + "  " + poemItem.getAuthor());
        evernoteShareContent.setShareImage(localImage);
        evernoteShareContent.setTargetUrl(appDownloadPage);
        mController.setShareMedia(evernoteShareContent);
    }


    public static String toURLEncoded(String paramString) {
        if (paramString == null || paramString.equals("")) {
            return "";
        }

        try {
            String str = new String(paramString.getBytes(), "UTF-8");
            str = URLEncoder.encode(str, "UTF-8");
            return str;
        } catch (Exception localException) {

        }

        return "";
    }

    /**
     * 配置分享平台参数</br>
     */
    private void configPlatforms() {
        // 添加QQ、QZone平台
        addQQQZonePlatform();

        // 添加微信、微信朋友圈平台
        addWXPlatform();

        //添加Evernote平台
        addEverNote();
    }


    /**
     * @return
     * @功能描述 : 添加微信平台分享
     */
    private void addWXPlatform() {
        // 注意：在微信授权的时候，必须传递appSecret
        // wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
        String appId = "wx967daebe835fbeac";
        String appSecret = "5bb696d9ccd75a38c8a0bfe0675559b3";
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(DetailActivity.this, appId, appSecret);
        wxHandler.addToSocialSDK();

        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(DetailActivity.this, appId, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
    }

    /**
     * @return
     * @功能描述 : 添加QQ平台支持 QQ分享的内容， 包含四种类型， 即单纯的文字、图片、音乐、视频. 参数说明 : title, summary,
     * image url中必须至少设置一个, targetUrl必须设置,网页地址必须以"http://"开头 . title :
     * 要分享标题 summary : 要分享的文字概述 image url : 图片地址 [以上三个参数至少填写一个] targetUrl
     * : 用户点击该分享时跳转到的目标地址 [必填] ( 若不填写则默认设置为友盟主页 )
     */
    private void addQQQZonePlatform() {
        String appId = "1104632240";
        String appKey = "nzcsq57JKtxdqZkb";
        // 添加QQ支持, 并且设置QQ分享内容的target url
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(DetailActivity.this,
                appId, appKey);
        qqSsoHandler.setTargetUrl("http://www.umeng.com/social");
        qqSsoHandler.addToSocialSDK();

        // 添加QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(DetailActivity.this, appId, appKey);
        qZoneSsoHandler.addToSocialSDK();
    }

    /**
     * 添加印象笔记平台
     */
    private void addEverNote() {
        UMEvernoteHandler evernoteHandler = new UMEvernoteHandler(DetailActivity.this);
        evernoteHandler.addToSocialSDK();
    }

}
