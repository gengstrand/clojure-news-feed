package info.glennengstrand.dao;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Friends")
public class Friend {
	@Id
	@GeneratedValue
	private Long friendsId;

	private Long fromParticipantId;
	private Long toParticipantId;
	
	public Friend() {
		super();
	}
	public Long getId() {
		return friendsId;
	}
	public void setId(Long id) {
		this.friendsId = id;
	}
	public Long getFromParticipantId() {
		return fromParticipantId;
	}
	public void setFromParticipantId(Long fromParticipantId) {
		this.fromParticipantId = fromParticipantId;
	}
	public Long getToParticipantId() {
		return toParticipantId;
	}
	public void setToParticipantId(Long toParticipantId) {
		this.toParticipantId = toParticipantId;
	}
	
}
