package com.xhr.Poem;

import android.content.Context;
import com.xhr.Poem.dal.CommentAccess;
import com.xhr.Poem.dal.PoemAccess;
import com.xhr.Poem.model.PoemItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xhrong on 2015/5/11.
 */
public class AppState {

    private static Context context;
    private static PoemAccess poemAccess;
    private static CommentAccess commentAccess;
    private static List<PoemItem> classicPoems = new ArrayList<PoemItem>();
    private static List<PoemItem> currentPoems = new ArrayList<PoemItem>();



    public static void initAppState(Context cxt) {
        context = cxt;
        poemAccess = new PoemAccess(context);
        commentAccess = new CommentAccess(context);
    }

    public static PoemAccess getPoemAccess() {
        return poemAccess;
    }

    public static CommentAccess getCommentAccess() {
        return commentAccess;
    }

    public static Context getContext() {
        return context;
    }

    public static List<PoemItem> getClassicPoems() {
        return classicPoems;
    }

    public static void setClassicPoems(List<PoemItem> poemItems) {
        classicPoems = poemItems;
    }

    public static List<PoemItem> getCurrentPoems() {
        return currentPoems;
    }

    public static void setCurrentPoems(List<PoemItem> poemItems) {
        currentPoems = poemItems;
    }
}
