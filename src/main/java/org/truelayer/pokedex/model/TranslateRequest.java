package org.truelayer.pokedex.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TranslateRequest {

    @JsonProperty("text")
    private String text;

    public TranslateRequest() {}

    public TranslateRequest(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
