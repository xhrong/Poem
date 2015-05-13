package com.xhr.Poem.view;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.xhr.Poem.AppConfig;
import com.xhr.Poem.AppState;
import com.xhr.Poem.MainActivity;
import com.xhr.Poem.R;
import com.xhr.Poem.model.CommentItem;
import com.xhr.Poem.model.PoemItem;
import com.xhr.Poem.util.FileDownloader;
import com.xhr.Poem.util.NetWorkUtil;
import com.xhr.Poem.util.StringUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by xhrong on 2015/5/11.
 */
public class ContentFragment extends Fragment {


    PoemItem poemItem;
    List<CommentItem> commentItems = new ArrayList<CommentItem>();
    boolean isCommentLoaded = false;


    TextView tvTitle, tvAuthor, tvContent, tvNotation, tvTranslation, tvAnalysis;
    Button btnBack, btnLove, btnComment, btnViewComment, btnAudio;

    private IPlayer player;
    private IShare share;

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
        initView(rootView);

        player = (IPlayer) getActivity();
        share = (IShare) getActivity();

        return rootView;
    }

    @Override
    public void onPause() {//如果突然电话到来，停止播放音乐
        btnAudio.setText("朗读");
        super.onPause();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //相当于Fragment的onResume
        } else {
            //相当于Fragment的onPause
            if (btnAudio != null)
                btnAudio.setText("朗读");
        }
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
                share.share(poemItem);
            }
        });

        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isCommentLoaded) {
                    commentItems = AppState.getCommentAccess().getComments(poemItem.getId());
                    isCommentLoaded = true;
                }
                showCommentDialog(poemItem);
            }
        });
        btnAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player.isPlaying()) {
                    player.stop();
                    btnAudio.setText("朗读");
                    return;
                }
                btnAudio.setText("正在加载");
                final String fileName = AppConfig.AUDIO_SAVE_PATH + "/" + poemItem.getAudioUrl();
                File file = new File(fileName);
                if (file.exists()) {
                    player.play(fileName);
                    btnAudio.setText("停止");
                } else {
                    int result = NetWorkUtil.checkConnectionState(getActivity());
                    if (result == NetWorkUtil.CONNECTIVITY_TYPE_NONE) {
                        Toast.makeText(getActivity(), "文件尚未下载，请联网使用该功能", Toast.LENGTH_LONG).show();
                        return;
                    } else if (result == NetWorkUtil.CONNECTIVITY_TYPE_OTHER) {
                        Toast.makeText(getActivity(), "当前网络不是WIFI，请注意流量", Toast.LENGTH_LONG).show();
                    }
                    File temp = new File(AppConfig.AUDIO_SAVE_PATH);
                    if (!temp.exists()) {
                        temp.mkdirs();
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String url = AppConfig.AUDIO_DOWNLOAD_URL.replace("{0}", poemItem.getAudioUrl().replace("mp3", "zip"));
                            int result = FileDownloader.download(url, AppConfig.AUDIO_SAVE_PATH, poemItem.getAudioUrl());
                            if (result == 0) {
                                btnAudio.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        player.play(fileName);
                                        btnAudio.setText("停止");
                                    }
                                });
                            } else {
                                btnAudio.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        btnAudio.setText("朗读");
                                        Toast.makeText(getActivity(), "文件下载失败，请重试", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    }).start();
                }
            }
        });

        btnViewComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isCommentLoaded) {
                    commentItems = AppState.getCommentAccess().getComments(poemItem.getId());
                    isCommentLoaded = true;
                }

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


    public interface IPlayer {
        void play(String fileName);

        void stop();

        void release();

        boolean isPlaying();
    }

    public interface IShare {
        void share(PoemItem poemItem);
    }
}
