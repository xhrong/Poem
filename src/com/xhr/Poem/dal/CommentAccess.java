package com.xhr.Poem.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.xhr.Poem.model.CommentItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xhrong on 2015/5/6.
 */
public class CommentAccess {
    private static XhrDbHelper dbHelper;

    private static final String TABLE_NAME = "comment";

    public CommentAccess(Context context) {
        dbHelper = XhrDbHelper.getInstance(context);
    }

    public long addComment(CommentItem commentItem) {
        ContentValues initValues = new ContentValues();
        initValues.put(CommentItem.POEM_ID_KEY, commentItem.getPoemId());
        initValues.put(CommentItem.CONTENT_KEY, commentItem.getContent());
        initValues.put(CommentItem.ADD_DATE_KEY, commentItem.getAddDate());
        initValues.put(CommentItem.AUDIO_FILE_KEY, commentItem.getAudioFile());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        long result = db.insert(TABLE_NAME, null, initValues);
        db.close();
        return result;
    }

    public List<CommentItem> getComments(int poemId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME+" where poem_id="+poemId+" order by id desc", null);
        List<CommentItem> commentItems = new ArrayList<CommentItem>();
        while (cursor.moveToNext()) {
            commentItems.add(generateCommentItem(cursor));
        }
        cursor.close();
        db.close();
        return commentItems;
    }


    private CommentItem generateCommentItem(Cursor cursor) {
        CommentItem commentItem = new CommentItem();
        commentItem.setId(cursor.getInt(cursor.getColumnIndexOrThrow(CommentItem.ID_KEY)));
        commentItem.setPoemId(cursor.getInt(cursor.getColumnIndexOrThrow(CommentItem.POEM_ID_KEY)));
        commentItem.setContent(cursor.getString(cursor.getColumnIndexOrThrow(CommentItem.CONTENT_KEY)));
        commentItem.setAddDate(cursor.getString(cursor.getColumnIndexOrThrow(CommentItem.ADD_DATE_KEY)));
        commentItem.setAudioFile(cursor.getString(cursor.getColumnIndexOrThrow(CommentItem.AUDIO_FILE_KEY)));
        return commentItem;
    }
}
