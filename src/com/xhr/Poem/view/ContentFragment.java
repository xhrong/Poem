package com.xhr.Poem.view;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
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
import com.xhr.Poem.AppState;
import com.xhr.Poem.MainActivity;
import com.xhr.Poem.R;
import com.xhr.Poem.model.CommentItem;
import com.xhr.Poem.model.PoemItem;
import com.xhr.Poem.util.FileDownloader;
import com.xhr.Poem.util.Player;
import com.xhr.Poem.util.StringUtil;

import java.io.File;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by xhrong on 2015/5/11.
 */
public class ContentFragment extends Fragment {

    private final static String appDownloadPage = "http://xhrong.xicp.net:14468/";

    private static UMSocialService mController;

    PoemItem poemItem;
    List<CommentItem> commentItems = new ArrayList<CommentItem>();


    TextView tvTitle, tvAuthor, tvContent, tvNotation, tvTranslation, tvAnalysis;
    Button btnBack, btnLove, btnComment, btnViewComment, btnAudio;

    private Player player;
    private boolean isPaused = false;

    public static ContentFragment newInstance(PoemItem poemItem) {
        return new ContentFragment(poemItem);
    }

    public ContentFragment(PoemItem poemItem) {
        this.poemItem = poemItem;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content, container, false);

        //  commentAccess = new CommentAccess(getActivity());
        try {
            commentItems = AppState.getCommentAccess().getComments(poemItem.getId());
            Log.e("COUNT:", commentItems.size() + "");
        } catch (Exception e) {

        }
        player = new Player();
        initView(rootView);

        // 配置需要分享的相关平台
        configPlatforms();

        return rootView;
    }

    /**
     * 根据不同的平台设置不同的分享内容</br>
     */
    private void setShareContent() {
        mController = UMServiceFactory.getUMSocialService("com.umeng.share");
        mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.EVERNOTE);
        UMImage localImage = new UMImage(getActivity(), R.drawable.icon);

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
        UMWXHandler wxHandler = new UMWXHandler(getActivity(), appId, appSecret);
        wxHandler.addToSocialSDK();

        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(getActivity(), appId, appSecret);
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
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(getActivity(),
                appId, appKey);
        qqSsoHandler.setTargetUrl("http://www.umeng.com/social");
        qqSsoHandler.addToSocialSDK();

        // 添加QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(getActivity(), appId, appKey);
        qZoneSsoHandler.addToSocialSDK();
    }

    /**
     * 添加印象笔记平台
     */
    private void addEverNote() {
        UMEvernoteHandler evernoteHandler = new UMEvernoteHandler(getActivity());
        evernoteHandler.addToSocialSDK();
    }


    private void initView(View rootView) {
        tvTitle = (TextView) rootView.findViewById(R.id.tv_title);
        tvAuthor = (TextView) rootView.findViewById(R.id.tv_author);
        tvContent = (TextView) rootView.findViewById(R.id.tv_content);
        tvNotation = (TextView) rootView.findViewById(R.id.tvNotation);
        tvTranslation = (TextView) rootView.findViewById(R.id.tvTranslation);
        tvAnalysis = (TextView) rootView.findViewById(R.id.tvAnalysis);
        btnBack = (Button) rootView.findViewById(R.id.btn_back);
        btnLove = (Button) rootView.findViewById(R.id.btn_favorite);
        btnComment = (Button) rootView.findViewById(R.id.btnComment);
        btnAudio = (Button) rootView.findViewById(R.id.btnAudio);
        btnViewComment = (Button) rootView.findViewById(R.id.btnViewComment);
        //  ivFontBig = (ImageView) findViewById(R.id.iv_font_big);
        //ivFontSmall = (ImageView) findViewById(R.id.iv_font_small);
        rootView.findViewById(R.id.ll_parent).setBackgroundResource(MainActivity.bgImgs[new Random().nextInt(8)]);

        tvTitle.setText(poemItem.getTitle());
        tvAuthor.setText(poemItem.getAuthor());
        if (poemItem.getIsLoved() == 1) {
            tvContent.setText(poemItem.getContent());
            tvNotation.setText("");
            tvTranslation.setText("");
            tvAnalysis.setText("");
        } else {
            tvContent.setText(Html.fromHtml(poemItem.getContent()));
            if (!StringUtil.isEmpty(poemItem.getNotation())) {
                tvNotation.setText(Html.fromHtml("<strong>注解：</strong><br>" + poemItem.getNotation()));
            } else {
                tvNotation.setText(Html.fromHtml("<strong>注解：</strong>暂无"));
            }
            if (!StringUtil.isEmpty(poemItem.getTranslation())) {
                tvTranslation.setText(Html.fromHtml("<strong>翻译：</strong><br>" + poemItem.getTranslation()));
            } else {
                tvTranslation.setText(Html.fromHtml("<strong>翻译：</strong>暂无"));
            }
            if (!StringUtil.isEmpty(poemItem.getAnalysis())) {
                tvAnalysis.setText(Html.fromHtml("<strong>赏析：</strong><br>" + poemItem.getAnalysis()));
            } else {
                tvAnalysis.setText(Html.fromHtml("<strong>赏析：</strong>暂无"));
            }
        }


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        btnLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 设置分享的内容
                setShareContent();
                mController.openShare(getActivity(), false);
            }
        });

        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCommentDialog(poemItem);
            }
        });
        btnAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String fileName = Environment.getExternalStorageDirectory() + "/" + poemItem.getAudioUrl();
                File file = new File(fileName);
                if (file.exists()) {
                    player.playUrl(fileName);
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                          //  String url = "http://files.cnblogs.com/files/GrateSea/" + poemItem.getAudioUrl().replace("mp3", "zip");
                            String url="http://files.cnblogs.com/files/GrateSea/w20100812053713500.zip";
                            FileDownloader.download(url, Environment.getExternalStorageDirectory().getAbsolutePath(), poemItem.getAudioUrl());
                            player.playUrl(fileName);
                        }
                    }).start();
                }
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
        CommonDialog.Builder builder = new CommonDialog.Builder(getActivity());


        if (commentItems.size() > 0) {
            final ListView listView = new ListView(getActivity());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            listView.setLayoutParams(lp);
            listView.setAdapter(new CommentAdapter(getActivity(), commentItems));
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
        CommonDialog.Builder builder = new CommonDialog.Builder(getActivity());
        final EditText editText = new EditText(getActivity());
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
                    temp.setContent(data);
                    temp.setPoemId(poemItem.getId());
                    commentItems.add(0, temp);
                    AppState.getCommentAccess().addComment(temp);

                    dialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), "评论内容不能为空", Toast.LENGTH_SHORT).show();
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
