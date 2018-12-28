package info.glennengstrand.dao.elasticsearch;

import java.util.stream.Stream;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboundSearchRepository extends ElasticsearchRepository<Outbound, String> {
	Stream<Outbound> findByStory(final String story);
}
