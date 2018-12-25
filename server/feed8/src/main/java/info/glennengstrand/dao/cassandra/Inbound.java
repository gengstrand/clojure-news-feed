package info.glennengstrand.dao.cassandra;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.io.Serializable;
import java.util.UUID;

@Table("Inbound")
public class Inbound implements Serializable {

	@PrimaryKey
	private NewsFeedItemKey key;
	private Long fromParticipantId;
	private String subject;
	private String story;
	
	public Inbound() {
		key = new NewsFeedItemKey();
	}
	public Long getParticipantId() {
		return key.getParticipantId();
	}
	public void setParticipantId(Long participantId) {
		key.setParticipantId(participantId);
	}
	public UUID getOccured() {
		return key.getOccurred();
	}
	public void setOccured(UUID occured) {
		key.setOccurred(occured);
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getStory() {
		return story;
	}
	public void setStory(String story) {
		this.story = story;
	}
	public Long getFromParticipantId() {
		return fromParticipantId;
	}
	public void setFromParticipantId(Long fromParticipantId) {
		this.fromParticipantId = fromParticipantId;
	}

}
