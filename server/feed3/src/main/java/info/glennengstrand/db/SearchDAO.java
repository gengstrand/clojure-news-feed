package info.glennengstrand.db;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public abstract class SearchDAO {

	public abstract List<Long> find(String keywords);
	public abstract void upsert(final UpsertRequest doc);
	
	public UpsertRequestBuilder getBuilder() {
		return new UpsertRequestBuilder();
	}
	
	public class UpsertRequest {
		private final String id;
		private final long sender;
		private final String story;

		public String getId() {
			return id;
		}

		public long getSender() {
			return sender;
		}

		public String getStory() {
			return story;
		}
		
		public UpsertRequest(long sender, String story) {
			id = UUID.randomUUID().toString();
			this.sender = sender;
			this.story = story;
		}
		
	}
	
	public class UpsertRequestBuilder {
		private long sender;
		private String story;
		public UpsertRequestBuilder withSender(long sender) {
			this.sender = sender;
			return this;
		}
		public UpsertRequestBuilder withStory(String story) {
			this.story = story;
			return this;
		}
		
		public UpsertRequest build() {
			return new UpsertRequest(sender, story);
		}
	}
	
	public static class DoNothingSearchDAO extends SearchDAO {

		@Override
		public List<Long> find(String keywords) {
			return Collections.emptyList();
		}

		@Override
		public void upsert(UpsertRequest doc) {
		}
		
	}
}
