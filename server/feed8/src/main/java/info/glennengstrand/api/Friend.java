package info.glennengstrand.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.validation.annotation.Validated;

@Validated
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
 public record Friend(
  @JsonProperty("id")
  Long id,
  @JsonProperty("from")
  String from,
  @JsonProperty("to")
  String to) {
}


