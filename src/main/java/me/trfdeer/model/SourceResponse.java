package me.trfdeer.model;

import java.util.List;

public class SourceResponse {
    public List<Source> sources;
    public List<String> folders;

    public SourceResponse(List<Source> sources, List<String> folders) {
        this.sources = sources;
        this.folders = folders;
    }
}
