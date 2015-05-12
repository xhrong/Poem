package com.xhr.Poem;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.xhr.Poem.model.PoemItem;
import com.xhr.Poem.util.StringUtil;

/**
 * Created by xhrong on 2015/5/9.
 */
public class AddPoemActivity extends Activity {

    EditText etTitle, etAuthor, etContent;
    Button btnOK, btnCancel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_poem);
        initView();
    }

    private void initView() {
        etTitle = (EditText) findViewById(R.id.etTitle);
        etAuthor = (EditText) findViewById(R.id.etAuthor);
        etContent = (EditText) findViewById(R.id.etContent);

        btnOK = (Button) findViewById(R.id.positiveButton);
        btnCancel = (Button) findViewById(R.id.negativeButton);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddPoemActivity.this.setResult(RESULT_CANCELED);
                AddPoemActivity.this.finish();
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = etTitle.getText().toString();
                String author = etAuthor.getText().toString();
                String content = etContent.getText().toString();
                if (StringUtil.isEmpty(title) || StringUtil.isEmpty(content)) {
                    Toast.makeText(AddPoemActivity.this, "标题和内容不能为空", Toast.LENGTH_LONG).show();
                    return;
                } else if (content.length() < 20) {
                    Toast.makeText(AddPoemActivity.this, "内容不能少于20个字", Toast.LENGTH_LONG).show();
                    return;
                }
                if (StringUtil.isEmpty(author)) {
                    author = "佚名";
                }
                PoemItem poemItem = new PoemItem();
                poemItem.setTitle(title);
                poemItem.setAuthor(author);
                poemItem.setContent(content);
                poemItem.setIsLoved(1);
                AppState.getPoemAccess().addPoem(poemItem);
                Toast.makeText(AddPoemActivity.this, "添加成功", Toast.LENGTH_LONG).show();
                AddPoemActivity.this.setResult(RESULT_OK);
                AddPoemActivity.this.finish();

            }
        });
    }
}
