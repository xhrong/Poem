package com.xhr.Poem;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.xhr.Poem.dal.CommentAccess;
import com.xhr.Poem.dal.PoemAccess;
import com.xhr.Poem.model.CommentItem;
import com.xhr.Poem.model.PoemItem;
import com.xhr.Poem.view.CommonDialog;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by xhrong on 2015/4/6.
 */
public class ContentActivity extends Activity {

    TextView tvTitle, tvAuthor, tvContent;
    Button btnBack, btnLove, btnComment, btnViewComment;
    // ImageView ivFontBig, ivFontSmall;

    PoemItem poemItem;

    List<CommentItem> commentItems = new ArrayList<CommentItem>();
    boolean canLoved = true;
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
        poemItem.setId(intent.getIntExtra("id", -1));
        canLoved = intent.getBooleanExtra("canLoved", true);

        commentAccess = new CommentAccess(this);
        poemAccess=new PoemAccess(this);
        //    commentJSONSerializer = new CommentJSONSerializer(this, "comment_" + ciInfo.id + ".json");
        try {
            commentItems = commentAccess.getComments(poemItem.getId());
            Log.e("COUNT:", commentItems.size() + "");
        } catch (Exception e) {

        }
        initView();


    }

    private void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvAuthor = (TextView) findViewById(R.id.tv_author);
        tvContent = (TextView) findViewById(R.id.tv_desc);
        btnBack = (Button) findViewById(R.id.btn_back);
        btnLove = (Button) findViewById(R.id.btn_favorite);
        btnComment = (Button) findViewById(R.id.btnComment);
        btnViewComment = (Button) findViewById(R.id.btnViewComment);
        //  ivFontBig = (ImageView) findViewById(R.id.iv_font_big);
        //ivFontSmall = (ImageView) findViewById(R.id.iv_font_small);
        findViewById(R.id.ll_parent).setBackgroundResource(MainActivity.bgImgs[new Random().nextInt(8)]);

        tvTitle.setText(poemItem.getTitle());
        tvAuthor.setText(tvAuthor.getText() + poemItem.getAuthor());
        tvContent.setText(Html.fromHtml(poemItem.getContent()));

        if (!canLoved) {
            btnLove.setVisibility(View.GONE);
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
                poemItem.setIsLoved(1);
                poemAccess.updatePoem(poemItem);
                Toast.makeText(ContentActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
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
                    commentItems.add(0,temp);
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
