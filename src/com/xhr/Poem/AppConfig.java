package com.xhr.Poem;

import android.os.Environment;

/**
 * Created by xhrong on 2015/5/13.
 */
public class AppConfig {

    public static final String APP_BASE_PATH = Environment.getExternalStorageDirectory() + "/com.xhr.Poem/";

    public static final String AUDIO_SAVE_PATH = APP_BASE_PATH + "audio/";

    public static final String TEMP_PATH = APP_BASE_PATH + "temp/";

    public static final String DB_PATH=APP_BASE_PATH+"db/";

    public static final String TEMP_IMAGE_NAME="temp.jpg";

    public static final String AUDIO_DOWNLOAD_URL = "http://files.cnblogs.com/files/GrateSea/{0}";

    public static final String ShARE_TARGET_URL = "https://github.com/xhrong/Common/blob/master/README.md";

    public static final String QQ_APPID = "1104632240";
}
