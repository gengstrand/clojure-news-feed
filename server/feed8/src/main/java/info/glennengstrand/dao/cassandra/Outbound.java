package info.glennengstrand.dao.cassandra;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.io.Serializable;
import java.util.UUID;

@Table("Outbound")
public class Outbound implements Serializable {

	@PrimaryKey
	private NewsFeedItemKey key;
	private String subject;
	private String story;
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
	
}
