package com.xhr.Poem;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import com.umeng.analytics.MobclickAgent;
import com.xhr.Poem.model.PoemItem;
import com.xhr.Poem.view.CommonDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by xhrong on 2015/4/6.
 */
public class MainActivity extends Activity {

    public static int[] bgImgs = {R.drawable.bg1, R.drawable.bg2, R.drawable.bg3, R.drawable.bg4, R.drawable.bg5, R.drawable.bg6, R.drawable.bg7, R.drawable.bg8};

    public static final int ADD_POEM_CODE = 0x001;

    private ListView listView;
    private Button btnSearch, btnAll, btnLove;
    private ImageButton btnAdd;


    private boolean isMyPoem = false;
    PoemAdapter poemAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        AppState.setClassicPoems(AppState.getPoemAccess().getAllPoems());
        AppState.getCurrentPoems().clear();
        AppState.getCurrentPoems().addAll(AppState.getClassicPoems());
        initView();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ADD_POEM_CODE:
                if (resultCode == RESULT_OK) {
                    if (isMyPoem) {
                        AppState.getCurrentPoems().clear();
                        AppState.getCurrentPoems().addAll(AppState.getPoemAccess().getLovedPoems());
                        poemAdapter.notifyDataSetChanged();
                    }
                } else if (resultCode == RESULT_CANCELED) {

                }
        }
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.lv_catelog);
        btnLove = (Button) findViewById(R.id.btn_favorite);
        btnAll = (Button) findViewById(R.id.btn_all);
        btnSearch = (Button) findViewById(R.id.btn_search);
        btnAdd = (ImageButton) findViewById(R.id.ibtn_add);

        findViewById(R.id.root).setBackgroundResource(bgImgs[new Random().nextInt(8)]);
        poemAdapter = new PoemAdapter(this, AppState.getCurrentPoems());
        listView.setAdapter(poemAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("position",i);
//                intent.putExtra("id", poemItems.get(i).getId());
//                intent.putExtra("isLoved", poemItems.get(i).getIsLoved());
//                intent.putExtra("title", poemItems.get(i).getTitle());
//                intent.putExtra("author", poemItems.get(i).getAuthor());
//                intent.putExtra("content", poemItems.get(i).getContent());
//                intent.putExtra("description", poemItems.get(i).getDescription());
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view,
                                           int position, long id) {
                if (isMyPoem) {
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
                showSearchDialog();
            }
        });

        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppState.getCurrentPoems().clear();
                AppState.getCurrentPoems().addAll(AppState.getClassicPoems());
                poemAdapter.notifyDataSetChanged();
                isMyPoem = false;
                setCurrent(btnAll);
            }
        });

        btnLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppState.getCurrentPoems().clear();
                AppState.getCurrentPoems().addAll(AppState.getPoemAccess().getLovedPoems());
                poemAdapter.notifyDataSetChanged();
                isMyPoem = true;
                setCurrent(btnLove);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDialog();
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
                AppState.getCurrentPoems().remove(poemItem);
                poemAdapter.notifyDataSetChanged();
                poemItem.setIsLoved(0);
                AppState.getPoemAccess().updatePoem(poemItem);
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

    public void showAddDialog() {
        Intent intent = new Intent(MainActivity.this, AddPoemActivity.class);
        startActivityForResult(intent, ADD_POEM_CODE);
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
                            for (PoemItem ciInfo : AppState.getClassicPoems()) {
                                if (ciInfo.getAuthor().contains(searchText))
                                    temp.add(ciInfo);
                            }
                        } else if (rb_title.isChecked()) {
                            for (PoemItem ciInfo : AppState.getClassicPoems()) {
                                if (ciInfo.getTitle().contains(searchText))
                                    temp.add(ciInfo);
                            }
                        } else {
                            for (PoemItem ciInfo : AppState.getClassicPoems()) {
                                if (ciInfo.getContent().contains(searchText))
                                    temp.add(ciInfo);
                            }
                        }
                        AppState.getCurrentPoems().clear();
                        AppState.getCurrentPoems().addAll(temp);
                        poemAdapter.notifyDataSetChanged();
                        isMyPoem = false;
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
