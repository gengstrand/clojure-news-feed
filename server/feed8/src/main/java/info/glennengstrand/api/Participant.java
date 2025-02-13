package info.glennengstrand.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.validation.annotation.Validated;

/**
 * Participant
 */
@Validated
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record Participant(
    @JsonProperty("id") Long id,
    @JsonProperty("name") String name,
    @JsonProperty("link") String link
) {
}
