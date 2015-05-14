package com.xhr.Poem;

import android.app.Application;
import com.xhr.Poem.dal.XhrDbHelper;

import java.io.File;

/**
 * Created by xhrong on 2015/5/6.
 */
public class XhrApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        createFolder();

        XhrDbHelper xhrDbHelper = XhrDbHelper.getInstance(getApplicationContext());
        xhrDbHelper.createDataBase();

        AppState.initAppState(getApplicationContext());
    }


    private void createFolder() {
        File temp = new File(AppConfig.AUDIO_SAVE_PATH);
        if (!temp.exists()) {
            temp.mkdirs();
        }
        temp = new File(AppConfig.TEMP_PATH);
        if (!temp.exists()) {
            temp.mkdirs();
        }
        temp=new File(AppConfig.DB_PATH);
        if(!temp.exists()){
            temp.mkdirs();
        }
    }
}
