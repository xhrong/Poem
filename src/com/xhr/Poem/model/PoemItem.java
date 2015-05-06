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
    public static final String DESCRIPTION_KEY = "description";


    private int id;
    private String title;
    private String author;
    private String category;
    private String content;
    private String description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
