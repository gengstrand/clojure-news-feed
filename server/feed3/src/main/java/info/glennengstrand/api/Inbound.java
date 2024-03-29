/**
 * News Feed
 * news feed api
 *
 * OpenAPI spec version: 1.0.0
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 *
 * Licensed under the Eclipse Public License - v 1.0
 *
 * https://www.eclipse.org/legal/epl-v10.html
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package info.glennengstrand.api;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.joda.time.DateTime;


/**
 * Inbound
 */

public class Inbound   {
  private String from = null;

  private String to = null;

  private String occurred = null;

  private String subject = null;

  private String story = null;

  private Inbound() {}
  public Inbound ( 
    @JsonProperty("from")
    String from,
    @JsonProperty("to")
    String to,
    @JsonProperty("occurred")
    String occurred,
    @JsonProperty("subject")
    String subject,
    @JsonProperty("story")
    String story
    ) {
      this.from = from;
      this.to = to;
      this.occurred = occurred;
      this.subject = subject;
      this.story = story;
    }
    /**
    * Get from
    * @return from
    **/
    @JsonProperty("from")
    @ApiModelProperty(value = "")
    public String getFrom() {
      return from;
    }
    /**
    * Get to
    * @return to
    **/
    @JsonProperty("to")
    @ApiModelProperty(value = "")
    public String getTo() {
      return to;
    }
    /**
    * Get occurred
    * @return occurred
    **/
    @JsonProperty("occurred")
    @ApiModelProperty(example = "null", value = "")
    public String getOccurred() {
      return occurred;
    }
    /**
    * Get subject
    * @return subject
    **/
    @JsonProperty("subject")
    @ApiModelProperty(value = "")
    public String getSubject() {
      return subject;
    }
    /**
    * Get story
    * @return story
    **/
    @JsonProperty("story")
    @ApiModelProperty(value = "")
    public String getStory() {
      return story;
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

  public static class InboundBuilder {
    private String from = null;
    private String to = null;
    private String occurred = null;
    private String subject = null;
    private String story = null;
    public InboundBuilder withFrom(String from) {
      this.from = from;
      return this;
    }
    public InboundBuilder withTo(String to) {
      this.to = to;
      return this;
    }
    public InboundBuilder withOccurred(String occurred) {
      this.occurred = occurred;
      return this;
    }
    public InboundBuilder withSubject(String subject) {
      this.subject = subject;
      return this;
    }
    public InboundBuilder withStory(String story) {
      this.story = story;
      return this;
    }
    public Inbound build() {
      return new Inbound (
        from, 
        to, 
        occurred, 
        subject, 
        story 
      );
    }
  }
}

