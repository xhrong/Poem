package com.xhr.Poem.model;

/**
 * Created by xhrong on 2015/5/6.
 */
public class CommentItem {


    public static final String ID_KEY = "id";
    public static final String POEM_ID_KEY = "poem_id";
    public static final String CONTENT_KEY = "content";
    public static final String ADD_DATE_KEY = "add_date";
    public static final String AUDIO_FILE_KEY = "audio_file";


    private int id;
    private int poemId;
    private String content;
    private String addDate;
    private String audioFile;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPoemId() {
        return poemId;
    }

    public void setPoemId(int poemId) {
        this.poemId = poemId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAddDate() {
        return addDate;
    }

    public void setAddDate(String addDate) {
        this.addDate = addDate;
    }

    public String getAudioFile() {
        return audioFile;
    }

    public void setAudioFile(String audioFile) {
        this.audioFile = audioFile;
    }
}
