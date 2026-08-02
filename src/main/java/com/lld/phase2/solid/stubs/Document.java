package com.lld.phase2.solid.stubs;

public class Document {

    private String id;
    private String name;
    private String content;

    public Document(String id, String name, String content) {
        this.id = id;
        this.name = name;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }
}
