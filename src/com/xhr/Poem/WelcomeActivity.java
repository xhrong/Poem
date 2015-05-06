package com.xhr.Poem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.xhr.Poem.dal.PoemAccess;


public class WelcomeActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PoemAccess poemAccess=new PoemAccess(WelcomeActivity.this);
                    XhrApplication.setPoemItemList(poemAccess.getAllPoems());
                    Thread.sleep(1000);
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(intent);
                    WelcomeActivity.this.finish();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();


    }

}
