package com.xhr.Poem.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by xhrong on 2015/5/12.
 */
public class FileDownloader {

    public static int download(String urlStr, String path, String fileName) {
        OutputStream outputStream = null;
        String tempFileName = fileName + ".temp";
        try {
            URL url = new URL(urlStr);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setConnectTimeout(5 * 1000);
            http.setRequestMethod("GET");
            http.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
            http.setRequestProperty("Accept-Language", "zh-CN");
            http.setRequestProperty("Referer", urlStr);
            http.setRequestProperty("Charset", "UTF-8");
            http.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
            http.setRequestProperty("Connection", "Keep-Alive");

            String pathName = path + "/" + tempFileName;//文件存储路径

            File tempFile = new File(pathName);

            if (tempFile.exists()) {
                tempFile.delete();
            }
            InputStream inStream = http.getInputStream();
            outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[1024 * 4];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.close();
            inStream.close();
            tempFile.renameTo(new File(path + "/" + fileName));
            return 0;
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        } finally {
            try {
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
