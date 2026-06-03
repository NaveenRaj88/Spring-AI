package org.spring.ai.springrag.model;

public record GameTitle (String title) {

    public String getNormalizedTitle() {
        return title.toLowerCase().replace(" ", "_");
    }
}
