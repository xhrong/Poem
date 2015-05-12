package com.xhr.Poem.model;

/**
 * Created by xhrong on 2015/5/5.
 */
public class PoemItem {


    public static final String ID_KEY = "id";
    public static final String TITLE_KEY = "title";
    public static final String AUTHOR_KEY = "author";
    public static final String CATEGORY_KEY = "category";
    public static final String CONTENT_KEY = "content";
    public static final String NOTATION_KEY = "notation";
    public static final String TRANSLATION_KEY = "translation";
    public static final String ANALYSIS_KEY = "analysis";
    public static final String AUDIO_URL_KEY = "audio_url";
    public static final String IMAGE_URL_KEY = "image_url";
    public static final String IS_LOVED_KEY = "is_loved";


    private int id;
    private String title="";
    private String author="";
    private String category="";
    private String content="";
    private String notation="";
    private String translation="";
    private String analysis="";
    private String audioUrl="";
    private String imageUrl="";
    private int isLoved;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNotation() {
        return notation;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getIsLoved() {
        return isLoved;
    }

    public void setIsLoved(int isLoved) {
        this.isLoved = isLoved;
    }
}
