package com.xhr.Poem;

import android.os.Environment;

/**
 * Created by xhrong on 2015/5/13.
 */
public class AppConfig {

    public static final String APP_BASE_PATH = Environment.getExternalStorageDirectory() + "/com.xhr.Poem";

    public static final String AUDIO_SAVE_PATH = APP_BASE_PATH + "/audio";

    public static final String AUDIO_DOWNLOAD_URL=  "http://files.cnblogs.com/files/GrateSea/{0}";

}
