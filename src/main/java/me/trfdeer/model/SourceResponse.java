package me.trfdeer.model;

import java.util.ArrayList;

public class SourceResponse {
    public ArrayList<Source> sources;
    public ArrayList<String> folders;

    public SourceResponse(ArrayList<Source> sources, ArrayList<String> folders) {
        this.sources = sources;
        this.folders = folders;
    }
}
