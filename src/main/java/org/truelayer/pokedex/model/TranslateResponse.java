package org.truelayer.pokedex.model;


import com.fasterxml.jackson.annotation.JsonProperty;

public class TranslateResponse {

    @JsonProperty("contents")
    private Contents contents;

    public TranslateResponse() {}

    public Contents getContents() {
        return contents;
    }

    public void setContents(Contents contents) {
        this.contents = contents;
    }
}
