package info.glennengstrand.dao.mysql;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Friends")
public class Friend {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "FriendsID")
	private Long friendsId;

	@Column(name = "FromParticipantID")
	private Long fromParticipantId;

	@Column(name = "ToParticipantID")
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
