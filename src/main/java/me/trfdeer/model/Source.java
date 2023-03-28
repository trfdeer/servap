package me.trfdeer.model;

public class Source {
    private int id;
    private String title;
    private String url;
    private String faviconUrl;
    private String folder;

    public Source(int id, String title, String url, String faviconUrl, String folder) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.faviconUrl = faviconUrl;
        this.folder = folder;
    }

    public int getId() {
        return this.id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getFaviconUrl() {
        return faviconUrl;
    }

    public String getFolder() {
        return folder;
    }

}