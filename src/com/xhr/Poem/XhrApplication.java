package com.xhr.Poem;

import android.app.Application;
import com.xhr.Poem.dal.XhrDbHelper;
import com.xhr.Poem.model.PoemItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xhrong on 2015/5/6.
 */
public class XhrApplication extends Application {

    private static List<PoemItem> poemItemList = new ArrayList<PoemItem>();

    public static List<PoemItem> getPoemItemList() {
        return poemItemList;
    }

    public static void setPoemItemList(List<PoemItem> poemItems) {
        poemItemList = poemItems;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        XhrDbHelper xhrDbHelper = XhrDbHelper.getInstance(getApplicationContext());
        xhrDbHelper.createDataBase();
    }
}
