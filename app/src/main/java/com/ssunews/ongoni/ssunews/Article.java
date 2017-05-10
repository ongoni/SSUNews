package com.ssunews.ongoni.ssunews;

public class Article {

    public String title;
    public String description;
    public String link;
    public String pubDate;
    public int guid;

    public Article (String Title, String Description, String PubDate, String Link, int Guid) {
        this.title = Title;
        this.description = Description;
        this.pubDate = PubDate;
        this.link = Link;
        this.guid = Guid;
    }
}