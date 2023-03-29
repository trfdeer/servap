package me.trfdeer.model;

import java.time.Instant;

public class Link {
    public int id;
    public int sourceId;
    public String title;
    public String link;
    public String description;
    public String author;
    public Instant publishDate;

    public Link(int id, int sourceId, String title, String link, String description, String author,
            Instant publishDate) {
        this.id = id;
        this.sourceId = sourceId;
        this.title = title;
        this.link = link;
        this.description = description;
        this.author = author;
        this.publishDate = publishDate;
    }
}
