package com.xhr.Poem;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.xhr.Poem.dal.PoemAccess;
import com.xhr.Poem.model.PoemItem;

import java.util.List;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        PoemAccess poemAccess = new PoemAccess(this);


        long startTime = System.currentTimeMillis(); //获取开始时间
        List<PoemItem> matchedPoems = poemAccess.getMatchedPoems(PoemItem.CONTENT_KEY, "好");
        long endTime = System.currentTimeMillis(); //获取结束时间
        Log.e("程序运行时间：", (endTime - startTime) + "ms"+ matchedPoems.size());

        matchedPoems.clear();
        startTime = System.currentTimeMillis(); //获取开始时间
        List<PoemItem>   allPoems = poemAccess.getAllPoems();
        endTime = System.currentTimeMillis(); //获取结束时间
        Log.e("程序运行时间：", (endTime - startTime) + "ms");


        startTime = System.currentTimeMillis(); //获取开始时间
        for (PoemItem item : allPoems) {
            if (item.getContent().contains("好")) {
                matchedPoems.add(item);
            }
        }
        endTime = System.currentTimeMillis(); //获取结束时间
        Log.e("程序运行时间：", (endTime - startTime) + "ms" + matchedPoems.size());


        Log.e("PoemCount:", matchedPoems.size() + "");
        ((TextView) findViewById(R.id.text)).setText(matchedPoems.size() + "");
    }
}
