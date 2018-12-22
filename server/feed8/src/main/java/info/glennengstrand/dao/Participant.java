package info.glennengstrand.dao;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Participant {
	@Id
	@GeneratedValue
	private Long participantId;
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
