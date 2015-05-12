package com.xhr.Poem.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.xhr.Poem.model.PoemItem;
import com.xhr.Poem.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xhrong on 2015/5/5.
 */
public class PoemAccess {

    private static XhrDbHelper dbHelper;

    private static final String TABLE_NAME = "poem";

    public PoemAccess(Context context) {
        dbHelper = XhrDbHelper.getInstance(context);
    }

    public long addPoem(PoemItem poem) {
        ContentValues initValues = new ContentValues();
        initValues.put(PoemItem.TITLE_KEY, poem.getTitle());
        initValues.put(PoemItem.AUTHOR_KEY, poem.getAuthor());
        initValues.put(PoemItem.CATEGORY_KEY, poem.getCategory());
        initValues.put(PoemItem.CONTENT_KEY, poem.getContent());
        initValues.put(PoemItem.NOTATION_KEY,poem.getNotation());
        initValues.put(PoemItem.TRANSLATION_KEY,poem.getTranslation());
        initValues.put(PoemItem.ANALYSIS_KEY,poem.getAnalysis());
        initValues.put(PoemItem.AUDIO_URL_KEY,poem.getAudioUrl());
        initValues.put(PoemItem.IMAGE_URL_KEY,poem.getImageUrl());
        initValues.put(PoemItem.IS_LOVED_KEY, poem.getIsLoved());

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        long result = db.insert(TABLE_NAME, null, initValues);
        db.close();
        return result;
    }

    public List<PoemItem> getLovedPoems() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where is_loved=1", null);
        List<PoemItem> lovedPoems = new ArrayList<PoemItem>();
        while (cursor.moveToNext()) {
            lovedPoems.add(generatePoemItem(cursor));
        }
        cursor.close();
        db.close();
        return lovedPoems;
    }

    public int updatePoem(PoemItem poem) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues initValues = new ContentValues();
        initValues.put(PoemItem.TITLE_KEY, poem.getTitle());
        initValues.put(PoemItem.AUTHOR_KEY, poem.getAuthor());
        initValues.put(PoemItem.CATEGORY_KEY, poem.getCategory());
        initValues.put(PoemItem.CONTENT_KEY, poem.getContent());
        initValues.put(PoemItem.NOTATION_KEY,poem.getNotation());
        initValues.put(PoemItem.TRANSLATION_KEY,poem.getTranslation());
        initValues.put(PoemItem.ANALYSIS_KEY,poem.getAnalysis());
        initValues.put(PoemItem.AUDIO_URL_KEY,poem.getAudioUrl());
        initValues.put(PoemItem.IMAGE_URL_KEY,poem.getImageUrl());
        initValues.put(PoemItem.IS_LOVED_KEY, poem.getIsLoved());
        int result = db.update(TABLE_NAME, initValues, "id=?", new String[]{String.valueOf(poem.getId())});
        db.close();
        return result;
    }



    public List<PoemItem> getMatchedPoems(String key, String value) {
        String sql = null;
        List<PoemItem> matchedPoems = new ArrayList<PoemItem>();
        //如果key或value为空，则返回空
        if (StringUtil.isEmpty(key) || StringUtil.isEmpty(value)) {
            return matchedPoems;
        }
        if (key.equals(PoemItem.AUTHOR_KEY)) {
            sql = "select * from " + TABLE_NAME + " where author like '%" + value + "%'";
        } else if (key.equals(PoemItem.TITLE_KEY)) {
            sql = "select * from " + TABLE_NAME + " where title like '%" + value + "%'";
        } else if (key.equals(PoemItem.CONTENT_KEY)) {
            sql = "select * from " + TABLE_NAME + " where content like '%" + value + "%'";
        }
        //如果不是上面三个查询，则返回空
        if (sql == null) {
            return matchedPoems;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            matchedPoems.add(generatePoemItem(cursor));
        }
        cursor.close();
        db.close();
        return matchedPoems;
    }


    public List<PoemItem> getAllPoems() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME +" where is_loved=0", null);
        List<PoemItem> allPoems = new ArrayList<PoemItem>();
        while (cursor.moveToNext()) {
            allPoems.add(generatePoemItem(cursor));
        }
        cursor.close();
        db.close();
        return allPoems;
    }

    private PoemItem generatePoemItem(Cursor cursor) {
        PoemItem poemItem = new PoemItem();
        poemItem.setId(cursor.getInt(cursor.getColumnIndexOrThrow(PoemItem.ID_KEY)));
        poemItem.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(PoemItem.TITLE_KEY)));
        poemItem.setAuthor(cursor.getString(cursor.getColumnIndexOrThrow(PoemItem.AUTHOR_KEY)));
        poemItem.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(PoemItem.CATEGORY_KEY)));
        poemItem.setContent(cursor.getString(cursor.getColumnIndexOrThrow(PoemItem.CONTENT_KEY)));
        poemItem.setNotation(cursor.getString(cursor.getColumnIndexOrThrow(PoemItem.NOTATION_KEY)));
        poemItem.setTranslation(cursor.getString(cursor.getColumnIndexOrThrow(PoemItem.TRANSLATION_KEY)));
        poemItem.setAnalysis(cursor.getString(cursor.getColumnIndexOrThrow(PoemItem.ANALYSIS_KEY)));
        poemItem.setAudioUrl(cursor.getString(cursor.getColumnIndexOrThrow(PoemItem.AUDIO_URL_KEY)));
        poemItem.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(PoemItem.IMAGE_URL_KEY)));
        poemItem.setIsLoved(cursor.getInt(cursor.getColumnIndexOrThrow(PoemItem.IS_LOVED_KEY)));
        return poemItem;
    }
}
