package com.xhr.Poem.dal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.xhr.Poem.AppConfig;

import java.io.*;

/**
 * Created by xhrong on 2015/4/21.
 */
public class XhrDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "tangshi.db"; //数据库名称
    private static String DB_PATH = AppConfig.DB_PATH;
    private static String ASSETS_NAME = "tangshi.db";
    private static final int DB_VERSION = 1;
    private Context mContext;

    private static String TAG = "XhrDbHelper";


//    private static final String CREATE_SENTENCES_TABLE = "CREATE TABLE [poem] (\n" +
//            "  [id] integer PRIMARY KEY autoincrement,\n" +
//            "  [title] TEXT, \n" +
//            "  [author] TEXT,   \n" +
//            "  [category] TEXT,\n" +
//            "  [content] TEXT, \n" +
//            "  [description] TEXT);";

    private static XhrDbHelper mInstance;

    public synchronized static XhrDbHelper getInstance(Context context) {

        if (mInstance == null) {
            mInstance = new XhrDbHelper(context);
        }
        return mInstance;
    }


    public XhrDbHelper(Context context) {
        super(context, DB_PATH + DB_NAME, null, DB_VERSION);
        mContext = context;
    }


    public void createDataBase() {
        boolean dbExist = checkDataBase();
        if (dbExist) {
            //数据库已存在，do nothing.
        } else {
            //创建数据库
            try {
                File dir = new File(DB_PATH);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File dbf = new File(DB_PATH + DB_NAME);
                if (dbf.exists()) {
                    dbf.delete();
                }
                copyDataBase();
            } catch (IOException e) {
                Log.e("创建数据库：", "数据库创建失败");
            }
        }
    }

    //检查数据库是否有效
    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        String myPath = DB_PATH + DB_NAME;
        try {
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            //database does't exist yet.
        } finally {
            if (checkDB != null) {
                checkDB.close();
            }
            return checkDB != null;
        }
    }

    private void copyDataBase() throws IOException {
        InputStream myInput = mContext.getAssets().open(ASSETS_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // db.execSQL(CREATE_SENTENCES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //    db.execSQL("DROP TABLE IF EXISTS sentences");
        onCreate(db);
    }
}

