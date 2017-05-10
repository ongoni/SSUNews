package com.ssunews.ongoni.ssunews;

public class Article {

    public String title;
    public String description;
    public String link;
    public String pubDate;
    public String guid;

    public Article (String Title, String Description, String PubDate, String Link, String Guid) {
        this.title = Title;
        this.description = Description;
        this.pubDate = PubDate;
        this.link = Link;
        this.guid = Guid;
    }
}