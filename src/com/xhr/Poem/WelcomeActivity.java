package com.xhr.Poem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.xhr.Poem.dal.PoemAccess;


public class WelcomeActivity extends Activity {

    private static boolean isCanceled = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PoemAccess poemAccess = new PoemAccess(WelcomeActivity.this);
                    AppState.setClassicPoems(poemAccess.getAllPoems());
                    Thread.sleep(1000);
                    if (!isCanceled) {
                        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                    WelcomeActivity.this.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isCanceled = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        isCanceled = true;
    }

}
