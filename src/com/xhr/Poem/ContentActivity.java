package com.xhr.Poem;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
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
import com.xhr.Poem.dal.CommentAccess;
import com.xhr.Poem.dal.PoemAccess;
import com.xhr.Poem.model.CommentItem;
import com.xhr.Poem.model.PoemItem;
import com.xhr.Poem.util.StringUtil;
import com.xhr.Poem.view.CommonDialog;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by xhrong on 2015/4/6.
 */
public class ContentActivity extends Activity {

    private final static String appDownloadPage = "http://xhrong.xicp.net:14468/";

    private static UMSocialService mController;


    TextView tvTitle, tvAuthor, tvContent, tvDescription;
    Button btnBack, btnLove, btnComment, btnViewComment;
    // ImageView ivFontBig, ivFontSmall;

    PoemItem poemItem;

    List<CommentItem> commentItems = new ArrayList<CommentItem>();

    CommentAccess commentAccess;
    PoemAccess poemAccess;

    private static final float MAX_FONT_SIZE = 40f;
    private static final float MIN_FONT_SIZE = 20f;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content);


        Intent intent = getIntent();
        if (intent == null) finish();

        poemItem = new PoemItem();
        poemItem.setTitle(intent.getStringExtra("title"));
        poemItem.setAuthor(intent.getStringExtra("author"));
        poemItem.setContent(intent.getStringExtra("content"));
        poemItem.setDescription(intent.getStringExtra("description"));
        poemItem.setIsLoved(intent.getIntExtra("isLoved",0));
        poemItem.setId(intent.getIntExtra("id", -1));


        commentAccess = new CommentAccess(this);
        poemAccess = new PoemAccess(this);
        try {
            commentItems = commentAccess.getComments(poemItem.getId());
            Log.e("COUNT:", commentItems.size() + "");
        } catch (Exception e) {

        }
        initView();

        // 配置需要分享的相关平台
        configPlatforms();
        // 设置分享的内容
        setShareContent();
    }

    /**
     * 根据不同的平台设置不同的分享内容</br>
     */
    private void setShareContent() {
        mController = UMServiceFactory.getUMSocialService("com.umeng.share");
        mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.EVERNOTE);
        UMImage localImage = new UMImage(ContentActivity.this, R.drawable.icon);

        // 设置微信朋友圈分享的内容
        CircleShareContent circleShareContent = new CircleShareContent();
        circleShareContent.setShareContent(poemItem.getContent().replace("<br />", "\r\n"));
        circleShareContent.setTitle(poemItem.getTitle() + "  " + poemItem.getAuthor());
        circleShareContent.setShareImage(localImage);
        circleShareContent.setTargetUrl(appDownloadPage);
        mController.setShareMedia(circleShareContent);

        //设置微信分享内容
        WeiXinShareContent weiXinShareContent = new WeiXinShareContent();
        weiXinShareContent.setShareContent(poemItem.getContent().replace("<br />", "\r\n"));
        weiXinShareContent.setTitle(poemItem.getTitle() + "  " + poemItem.getAuthor());
        weiXinShareContent.setTargetUrl(appDownloadPage);
        weiXinShareContent.setShareMedia(localImage);
        mController.setShareMedia(weiXinShareContent);

        // 设置QQ空间分享内容
        QZoneShareContent qZoneShareContent = new QZoneShareContent();
        qZoneShareContent.setShareContent(poemItem.getContent().replace("<br />", "\r\n"));
        qZoneShareContent.setShareImage(localImage);
        qZoneShareContent.setTargetUrl(appDownloadPage);
        qZoneShareContent.setTitle(poemItem.getTitle() + "  " + poemItem.getAuthor());
        mController.setShareMedia(qZoneShareContent);

        //设置QQ分享内容
        QQShareContent qqShareContent = new QQShareContent();
        qqShareContent.setShareContent(poemItem.getContent().replace("<br />", "\r\n"));
        qqShareContent.setShareImage(localImage);
        qqShareContent.setTitle(poemItem.getTitle() + "  " + poemItem.getAuthor());
        String targetUrl = appDownloadPage + "?title=" +toURLEncoded(poemItem.getTitle())
                +"&author="+toURLEncoded(poemItem.getAuthor())
                +"&content="+ toURLEncoded(poemItem.getContent().replace("<br />","<br>"));
        qqShareContent.setTargetUrl(targetUrl);
        mController.setShareMedia(qqShareContent);

        // 设置evernote的分享内容
        EvernoteShareContent evernoteShareContent = new EvernoteShareContent(poemItem.getContent().replace("<br />", "\r\n"));
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
        UMWXHandler wxHandler = new UMWXHandler(ContentActivity.this, appId, appSecret);
        wxHandler.addToSocialSDK();

        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(ContentActivity.this, appId, appSecret);
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
        String appId = "100424468";
        String appKey = "c7394704798a158208a74ab60104f0ba";
        // 添加QQ支持, 并且设置QQ分享内容的target url
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(ContentActivity.this,
                appId, appKey);
        qqSsoHandler.setTargetUrl("http://www.umeng.com/social");
        qqSsoHandler.addToSocialSDK();

        // 添加QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(ContentActivity.this, appId, appKey);
        qZoneSsoHandler.addToSocialSDK();
    }

    /**
     * 添加印象笔记平台
     */
    private void addEverNote() {
        UMEvernoteHandler evernoteHandler = new UMEvernoteHandler(ContentActivity.this);
        evernoteHandler.addToSocialSDK();
    }


    private void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvAuthor = (TextView) findViewById(R.id.tv_author);
        tvContent = (TextView) findViewById(R.id.tv_content);
        tvDescription = (TextView) findViewById(R.id.tv_desc);
        btnBack = (Button) findViewById(R.id.btn_back);
        btnLove = (Button) findViewById(R.id.btn_favorite);
        btnComment = (Button) findViewById(R.id.btnComment);
        btnViewComment = (Button) findViewById(R.id.btnViewComment);
        //  ivFontBig = (ImageView) findViewById(R.id.iv_font_big);
        //ivFontSmall = (ImageView) findViewById(R.id.iv_font_small);
        findViewById(R.id.ll_parent).setBackgroundResource(MainActivity.bgImgs[new Random().nextInt(8)]);

        tvTitle.setText(poemItem.getTitle());
        tvAuthor.setText(poemItem.getAuthor());
        if(poemItem.getIsLoved()==1){
            tvContent.setText(poemItem.getContent());
            tvDescription.setText(poemItem.getDescription());
        }else{
            tvContent.setText(Html.fromHtml(poemItem.getContent()));
            if(!StringUtil.isEmpty(poemItem.getDescription())){
                tvDescription.setText(Html.fromHtml(poemItem.getDescription()));
            }else{
                tvDescription.setText("");
            }
        }



        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentActivity.this.finish();
            }
        });

        btnLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mController.openShare(ContentActivity.this, false);
            }
        });

        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCommentDialog(poemItem);
            }
        });

        btnViewComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showViewCommentDialog(poemItem);

            }
        });

    }


    public void showViewCommentDialog(final PoemItem poemItem) {
        CommonDialog.Builder builder = new CommonDialog.Builder(this);


        if (commentItems.size() > 0) {
            final ListView listView = new ListView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            listView.setLayoutParams(lp);
            listView.setAdapter(new CommentAdapter(this, commentItems));
            builder.setContentView(listView);
        } else {
            builder.setMessage("暂无评论");
        }

        builder.setTitle("查看评论");
        builder.setNegativeButton("返回",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        );

        builder.create().show();
    }

    public void showCommentDialog(final PoemItem poemItem) {
        CommonDialog.Builder builder = new CommonDialog.Builder(this);
        final EditText editText = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(10, 15, 10, 10);
        //  editText.setPadding(10,5,10,5);
        editText.setLayoutParams(lp);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
        editText.setSingleLine(false);
        editText.setMinLines(2);
        editText.setHint("请输入评论内容");
        builder.setContentView(editText);
        builder.setTitle("添加评论");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String data = editText.getText().toString();
                if (data != null && !data.equals("")) {
                    CommentItem temp = new CommentItem();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    temp.setAddDate(format.format(new Date()));
                    //  temp.setid = System.currentTimeMillis() + "";
                    temp.setContent(data);
                    temp.setPoemId(poemItem.getId());
                    commentItems.add(0, temp);
                    commentAccess.addComment(temp);

                    dialog.dismiss();
                } else {
                    Toast.makeText(ContentActivity.this, "评论内容不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        );

        builder.create().show();
    }


    public class CommentAdapter extends BaseAdapter {
        private Context mContext = null;
        private List<CommentItem> mCommentInfos = null;

        public CommentAdapter(Context context, List<CommentItem> commentInfos) {
            mContext = context;
            mCommentInfos = commentInfos;
        }

        @Override
        public int getCount() {
            int count = 0;
            if (null != mCommentInfos) {
                count = mCommentInfos.size();
            }
            return count;
        }

        @Override
        public CommentItem getItem(int position) {
            CommentItem item = null;

            if (null != mCommentInfos) {
                item = mCommentInfos.get(position);
            }

            return item;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (null == convertView) {
                viewHolder = new ViewHolder();
                LayoutInflater mInflater = LayoutInflater.from(mContext);
                convertView = mInflater.inflate(R.layout.comment_item, null);

                viewHolder.data = (TextView) convertView.findViewById(R.id.tv_data);
                viewHolder.createDate = (TextView) convertView
                        .findViewById(R.id.tv_createDate);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            // set item values to the viewHolder:

            CommentItem commentInfo = getItem(position);
            if (null != commentInfo) {
                viewHolder.data.setText(commentInfo.getContent());
                viewHolder.createDate.setText(commentInfo.getAddDate());

            }

            return convertView;
        }

    }

    private static class ViewHolder {
        TextView data;
        TextView createDate;
    }
}
