package org.truelayer.pokedex.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class PokemonResponse {
    @JsonProperty("name")
    private String name;

    @JsonProperty("flavor_text_entries")
    private List<FlavorTextEntry> flavorTextEntries;

    @JsonProperty("habitat")
    private Habitat habitat;

    @JsonProperty("is_legendary")
    private boolean isLegendary;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FlavorTextEntry> getFlavorTextEntries() {
        return flavorTextEntries;
    }

    public void setFlavorTextEntries(List<FlavorTextEntry> flavorTextEntries) {
        this.flavorTextEntries = flavorTextEntries;
    }

    public Habitat getHabitat() {
        return habitat;
    }

    public void setHabitat(Habitat habitat) {
        this.habitat = habitat;
    }

    public boolean isLegendary() {
        return isLegendary;
    }

    public void setLegendary(boolean legendary) {
        isLegendary = legendary;
    }

    public PokemonResponse() {
    }

    public PokemonResponse (String name, List<FlavorTextEntry> flavorTextEntries, Habitat habitat, boolean isLegendary) {
        this.name = name;
        this.flavorTextEntries = flavorTextEntries;
        this.habitat = habitat;
        this.isLegendary = isLegendary;
    }

    /**
     * Returns the first English description available, replacing unwanted characters.
     * @return the English description or an empty string if not found.
     */
    public String getEnglishDescription() {
        if (flavorTextEntries == null) {
            return "";
        }

        for (FlavorTextEntry entry : flavorTextEntries) {
            if ("en".equals(entry.getLanguage().getName())) {
                // Replace unwanted characters like "\n" and "\f"
                return entry.getFlavorText()
                        .replace("\n", " ")
                        .replace("\f", " ");
            }
        }

        return "";
    }

}
