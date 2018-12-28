package info.glennengstrand.dao.elasticsearch;

import java.util.UUID;

import org.springframework.data.annotation.Id;

import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "feed", type = "stories")
public class Outbound {

	@Id
	private String id = UUID.randomUUID().toString();
	
	private Integer sender;
	
	private String story;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getSender() {
		return sender;
	}

	public void setSender(Integer sender) {
		this.sender = sender;
	}

	public String getStory() {
		return story;
	}

	public void setStory(String story) {
		this.story = story;
	}
	
	
}
