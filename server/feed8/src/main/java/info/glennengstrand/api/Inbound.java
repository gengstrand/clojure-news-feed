package info.glennengstrand.api;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;

/**
 * Inbound represents an inbound message with details like sender, recipient,
 * occurrence date, subject, and the story or content of the message.
 */
@Validated
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record Inbound(
    @JsonProperty("from")
    @Schema(description = "Sender's identifier")
    String from,

    @JsonProperty("to")
    @Schema(description = "Recipient's identifier")
    String to,

    @JsonProperty("occurred")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "Date of occurrence")
    LocalDate occurred,

    @JsonProperty("subject")
    @Schema(description = "Subject or title of the message")
    String subject,

    @JsonProperty("story")
    @Schema(description = "Content or body of the message")
    String story
) {
}

