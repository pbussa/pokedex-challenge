package org.truelayer.pokedex.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Contents {

    @JsonProperty("text")
    private String text;

    @JsonProperty("translated")
    private String translated;

    @JsonProperty("translation")
    private String translation;

    public Contents() {}

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTranslated() {
        return translated;
    }

    public void setTranslated(String translated) {
        this.translated = translated;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }
}
