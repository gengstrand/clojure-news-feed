package info.glennengstrand.api;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.threeten.bp.OffsetDateTime;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Inbound
 */
@Validated

public class Inbound   {
  @JsonProperty("from")
  private String from = null;

  @JsonProperty("to")
  private String to = null;

  @JsonProperty("occurred")
  private OffsetDateTime occurred = null;

  @JsonProperty("subject")
  private String subject = null;

  @JsonProperty("story")
  private String story = null;

  public Inbound from(String from) {
    this.from = from;
    return this;
  }

  /**
   * Get from
   * @return from
  **/
  @ApiModelProperty(value = "")


  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public Inbound to(String to) {
    this.to = to;
    return this;
  }

  /**
   * Get to
   * @return to
  **/
  @ApiModelProperty(value = "")


  public String getTo() {
    return to;
  }

  public void setTo(String to) {
    this.to = to;
  }

  public Inbound occurred(OffsetDateTime occurred) {
    this.occurred = occurred;
    return this;
  }

  /**
   * Get occurred
   * @return occurred
  **/
  @ApiModelProperty(value = "")

  @Valid

  public OffsetDateTime getOccurred() {
    return occurred;
  }

  public void setOccurred(OffsetDateTime occurred) {
    this.occurred = occurred;
  }

  public Inbound subject(String subject) {
    this.subject = subject;
    return this;
  }

  /**
   * Get subject
   * @return subject
  **/
  @ApiModelProperty(value = "")


  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public Inbound story(String story) {
    this.story = story;
    return this;
  }

  /**
   * Get story
   * @return story
  **/
  @ApiModelProperty(value = "")


  public String getStory() {
    return story;
  }

  public void setStory(String story) {
    this.story = story;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Inbound inbound = (Inbound) o;
    return Objects.equals(this.from, inbound.from) &&
        Objects.equals(this.to, inbound.to) &&
        Objects.equals(this.occurred, inbound.occurred) &&
        Objects.equals(this.subject, inbound.subject) &&
        Objects.equals(this.story, inbound.story);
  }

  @Override
  public int hashCode() {
    return Objects.hash(from, to, occurred, subject, story);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Inbound {\n");
    
    sb.append("    from: ").append(toIndentedString(from)).append("\n");
    sb.append("    to: ").append(toIndentedString(to)).append("\n");
    sb.append("    occurred: ").append(toIndentedString(occurred)).append("\n");
    sb.append("    subject: ").append(toIndentedString(subject)).append("\n");
    sb.append("    story: ").append(toIndentedString(story)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

