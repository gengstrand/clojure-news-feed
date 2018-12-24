package info.glennengstrand.dao.mysql;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "Participant")
public class Participant {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ParticipantID")
	private Long participantId;
	
	@Column(name = "Moniker")
	private String moniker;
	
	public Participant() {
		super();
	}
	public Participant(Long id, String moniker) {
		this.participantId = id;
		this.moniker = moniker;
	}
	public Long getId() {
		return participantId;
	}
	public void setId(Long id) {
		this.participantId = id;
	}
	public String getMoniker() {
		return moniker;
	}
	public void setMoniker(String moniker) {
		this.moniker = moniker;
	}
	
}
