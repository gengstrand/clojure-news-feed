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
 * Outbound
 */
@Validated

public class Outbound   {
  @JsonProperty("from")
  private Long from = null;

  @JsonProperty("occurred")
  private OffsetDateTime occurred = null;

  @JsonProperty("subject")
  private String subject = null;

  @JsonProperty("story")
  private String story = null;

  public Outbound from(Long from) {
    this.from = from;
    return this;
  }

  /**
   * Get from
   * @return from
  **/
  @ApiModelProperty(value = "")


  public Long getFrom() {
    return from;
  }

  public void setFrom(Long from) {
    this.from = from;
  }

  public Outbound occurred(OffsetDateTime occurred) {
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

  public Outbound subject(String subject) {
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

  public Outbound story(String story) {
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
    Outbound outbound = (Outbound) o;
    return Objects.equals(this.from, outbound.from) &&
        Objects.equals(this.occurred, outbound.occurred) &&
        Objects.equals(this.subject, outbound.subject) &&
        Objects.equals(this.story, outbound.story);
  }

  @Override
  public int hashCode() {
    return Objects.hash(from, occurred, subject, story);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Outbound {\n");
    
    sb.append("    from: ").append(toIndentedString(from)).append("\n");
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

