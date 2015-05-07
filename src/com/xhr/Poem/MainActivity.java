package com.xhr.Poem;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.xhr.Poem.dal.PoemAccess;
import com.xhr.Poem.model.PoemItem;
import com.xhr.Poem.view.CommonDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by xhrong on 2015/4/6.
 */
public class MainActivity extends Activity {

    // 首先在您的Activity中添加如下成员变量
    final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");

    private Context mContext;

    private ListView listView;
    private Button btnSearch, btnAll, btnLove;
    private static List<PoemItem> poemItems = new ArrayList<PoemItem>();
    private boolean canLoved = true;
    PoemAdapter poemAdapter;

    PoemAccess poemAccess;
    public static int[] bgImgs = {R.drawable.bg1, R.drawable.bg2, R.drawable.bg3, R.drawable.bg4, R.drawable.bg5, R.drawable.bg6, R.drawable.bg7, R.drawable.bg8};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mContext = this;
        poemAccess = new PoemAccess(this);
        poemItems.clear();
        poemItems.addAll(XhrApplication.getPoemItemList());
        initView();
        initConfig();
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.lv_catelog);
        btnLove = (Button) findViewById(R.id.btn_favorite);
        btnAll = (Button) findViewById(R.id.btn_all);
        btnSearch = (Button) findViewById(R.id.btn_search);
        findViewById(R.id.root).setBackgroundResource(bgImgs[new Random().nextInt(8)]);
        ;
        poemAdapter = new PoemAdapter(this, poemItems);
        listView.setAdapter(poemAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, ContentActivity.class);
                intent.putExtra("id", poemItems.get(i).getId());
                intent.putExtra("canLoved", canLoved);
                intent.putExtra("title", poemItems.get(i).getTitle());
                intent.putExtra("author", poemItems.get(i).getAuthor());
                intent.putExtra("content", poemItems.get(i).getContent());
                intent.putExtra("description", poemItems.get(i).getDescription());
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view,
                                           int position, long id) {
                if (!canLoved) {
                    PoemItem temp = poemAdapter.getItem(position);
                    showDeleteDialog(temp);
                }
                return false;
            }
        });


        listView.setEmptyView((TextView) findViewById(R.id.noData));

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //    showSearchDialog();

                // 是否只有已登录用户才能打开分享选择页
                mController.openShare(MainActivity.this, false);
            }
        });

        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                poemItems.clear();
                poemItems.addAll(XhrApplication.getPoemItemList());
                poemAdapter.notifyDataSetChanged();
                canLoved = true;
                setCurrent(btnAll);
            }
        });

        btnLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                poemItems.clear();
                poemItems.addAll(poemAccess.getLovedPoems());
                poemAdapter.notifyDataSetChanged();
                canLoved = false;
                setCurrent(btnLove);
            }
        });

        setCurrent(btnAll);
    }


    private void setCurrent(Button btn) {
        btnLove.setBackgroundResource(R.drawable.btn_button);
        btnSearch.setBackgroundResource(R.drawable.btn_button);
        btnAll.setBackgroundResource(R.drawable.btn_button);
        btn.setBackgroundResource(R.drawable.ic_button_cur_normal);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showExitDialog();
        }
        return false;
    }

    public void showExitDialog() {
        CommonDialog.Builder builder = new CommonDialog.Builder(this);
        builder.setMessage("确定退出应用吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                MainActivity.this.finish();
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

    public void showSearchDialog() {
        new SearchDialog(MainActivity.this, R.style.Dialog).show();
    }

    public void showDeleteDialog(final PoemItem poemItem) {
        CommonDialog.Builder builder = new CommonDialog.Builder(this);
        builder.setMessage("确定从最爱中移除这首词作？");
        builder.setTitle("提示");
        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                poemItems.remove(poemItem);
                poemAdapter.notifyDataSetChanged();
                poemItem.setIsLoved(0);
                poemAccess.updatePoem(poemItem);
                //设置你的操作事项
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


    public class SearchDialog extends Dialog {
        Context context;

        EditText et_word;
        Button btn_search;
        RadioButton rb_auth, rb_title, rb_content;

        public SearchDialog(Context context) {
            super(context);
            this.context = context;
        }

        public SearchDialog(Context context, int theme) {
            super(context, theme);
            this.context = context;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            // TODO Auto-generated method stub
            super.onCreate(savedInstanceState);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.search_dialog, null);
            this.addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));


            btn_search = (Button) findViewById(R.id.btn_search);
            et_word = (EditText) findViewById(R.id.et_word);
            rb_auth = (RadioButton) findViewById(R.id.radio0);
            rb_title = (RadioButton) findViewById(R.id.radio1);
            rb_content = (RadioButton) findViewById(R.id.radio2);
            btn_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String searchText = et_word.getText().toString();
                    if (searchText != null && !searchText.equals("")) {
                        List<PoemItem> temp = new ArrayList<PoemItem>();
                        if (rb_auth.isChecked()) {
                            for (PoemItem ciInfo : XhrApplication.getPoemItemList()) {
                                if (ciInfo.getAuthor().contains(searchText))
                                    temp.add(ciInfo);
                            }
                        } else if (rb_title.isChecked()) {
                            for (PoemItem ciInfo : XhrApplication.getPoemItemList()) {
                                if (ciInfo.getTitle().contains(searchText))
                                    temp.add(ciInfo);
                            }
                        } else {
                            for (PoemItem ciInfo : XhrApplication.getPoemItemList()) {
                                if (ciInfo.getContent().contains(searchText))
                                    temp.add(ciInfo);
                            }
                        }
                        poemItems.clear();
                        poemItems.addAll(temp);
                        poemAdapter.notifyDataSetChanged();
                        canLoved = true;
                        SearchDialog.this.dismiss();
                        setCurrent(btnSearch);
                    }
                }
            });

        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        MobclickAgent.onResume(this);
    }

    /**
     * @功能描述 : 初始化与SDK相关的成员变量
     */
    private void initConfig() {
        mContext = MainActivity.this;
        //   mController = UMServiceFactory.getUMSocialService(DESCRIPTOR);

        // 要分享的文字内容
        String mShareContent = "小巫CSDN博客客户端，CSDN移动开发专家——IT_xiao小巫的专属客户端，你值得拥有。";
        mController.setShareContent(mShareContent);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.bg1);

        UMImage mUMImgBitmap = new UMImage(mContext, bitmap);
        mController.setShareImage(mUMImgBitmap);
        mController.setAppWebSite(""); // 设置应用地址

//        // 添加新浪和qq空间的SSO授权支持
//        mController.getConfig().setSsoHandler(new SinaSsoHandler());
//        // 添加腾讯微博SSO支持
//        mController.getConfig().setSsoHandler(new TencentWBSsoHandler());

        // wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
        String appID = "wx880cb2b22509cf25";
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(mContext, appID);
        wxHandler.addToSocialSDK();
        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(mContext, appID);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();

        // 设置微信好友分享内容
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        // 设置分享文字
        weixinContent.setShareContent(mShareContent);
        // 设置title
        weixinContent.setTitle("小巫CSDN博客客户端");
        // 设置分享内容跳转URL
        weixinContent.setTargetUrl("你的http://blog.csdn.net/wwj_748链接");
        // 设置分享图片
        weixinContent.setShareImage(mUMImgBitmap);
        mController.setShareMedia(weixinContent);

        // 设置微信朋友圈分享内容
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(mShareContent);
        // 设置朋友圈title
        circleMedia.setTitle("小巫CSDN博客客户端");
        circleMedia.setShareImage(mUMImgBitmap);
        circleMedia.setTargetUrl("你的http://blog.csdn.net/wwj_748链接");
        mController.setShareMedia(circleMedia);
//
        // 参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(MainActivity.this,"1102369913", "62ru775qbkentOUp");
        qqSsoHandler.addToSocialSDK();

//        // 参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
//        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(mContext,
//                "1102369913", "62ru775qbkentOUp");
//        qZoneSsoHandler.addToSocialSDK();


        QQShareContent qqShareContent = new QQShareContent();
        qqShareContent.setShareContent(mShareContent);
        qqShareContent.setTitle("小巫CSDN博客");
        qqShareContent.setShareImage(mUMImgBitmap);
        qqShareContent.setTargetUrl("http://blog.csdn.net/wwj_748");
        mController.setShareMedia(qqShareContent);

        QZoneShareContent qzone = new QZoneShareContent();
        // 设置分享文字
        qzone.setShareContent(mShareContent);
        // 设置点击消息的跳转URL
        qzone.setTargetUrl("http://blog.csdn.net/wwj_748");
        // 设置分享内容的标题
        qzone.setTitle("小巫CSDN博客");
        // 设置分享图片
        qzone.setShareImage(mUMImgBitmap);
        mController.setShareMedia(qzone);

    }

    public class PoemAdapter extends BaseAdapter {
        private Context mContext = null;
        private List<PoemItem> poemItems = null;

        public PoemAdapter(Context context, List<PoemItem> poemItems) {
            mContext = context;
            this.poemItems = poemItems;
        }

        @Override
        public int getCount() {
            int count = 0;
            if (null != poemItems) {
                count = poemItems.size();
            }
            return count;
        }

        @Override
        public PoemItem getItem(int position) {
            PoemItem item = null;

            if (null != poemItems) {
                item = poemItems.get(position);
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
                convertView = mInflater.inflate(R.layout.list_item, null);

                viewHolder.title = (TextView) convertView.findViewById(R.id.tv_title);
                viewHolder.author = (TextView) convertView
                        .findViewById(R.id.tv_auth);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            // set item values to the viewHolder:

            PoemItem poemItem = getItem(position);
            if (null != poemItem) {
                viewHolder.title.setText(poemItem.getTitle());
                viewHolder.author.setText(poemItem.getAuthor());

            }

            return convertView;
        }

    }

    private static class ViewHolder {
        TextView title;
        TextView author;
    }
}
