package org.truelayer.pokedex.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FlavorTextEntry {
    @JsonProperty("flavor_text")
    private String flavorText;

    @JsonProperty("language")
    private Language language;

    public String getFlavorText() {
        return flavorText;
    }

    public void setFlavorText(String flavorText) {
        this.flavorText = flavorText;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }
}
