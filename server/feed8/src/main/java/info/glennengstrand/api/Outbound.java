package info.glennengstrand.api;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;

/**
 * Outbound
 */
@Validated
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record Outbound(
    @JsonProperty("from")
    @Schema(description = "sender's identifier")
    String from,
    
    @JsonProperty("occurred")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "the day that this was sent")
    LocalDate occurred,
    
    @JsonProperty("subject")
    @Schema(description = "the subject or summary")
    String subject,
    
    @JsonProperty("story")
    @Schema(description = "the content")
    String story
) {
}

