package com.xhr.Poem;

import android.app.Application;
import com.xhr.Poem.dal.XhrDbHelper;

/**
 * Created by xhrong on 2015/5/6.
 */
public class XhrApplication extends Application {
    @Override
    public void onCreate() {

        XhrDbHelper xhrDbHelper=XhrDbHelper.getInstance(getApplicationContext());
        xhrDbHelper.createDataBase();
     }
}
