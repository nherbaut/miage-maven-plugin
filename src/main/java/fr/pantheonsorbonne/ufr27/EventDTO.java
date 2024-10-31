package fr.pantheonsorbonne.ufr27;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public record EventDTO(@JsonProperty(defaultValue = "") String source,
                       @JsonProperty(defaultValue = "") String application,
                       @JsonProperty(defaultValue = "") String userID, String type, String payload) {
}
