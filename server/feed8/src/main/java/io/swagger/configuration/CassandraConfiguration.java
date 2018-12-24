package io.swagger.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.SessionFactory;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.CassandraSessionFactoryBean;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.CassandraAdminTemplate;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.convert.MappingCassandraConverter;
import org.springframework.data.cassandra.core.mapping.BasicCassandraMappingContext;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@EnableCassandraRepositories("info.glennengstrand.dao.cassandra")
public class CassandraConfiguration extends AbstractCassandraConfiguration {

	@Value("${spring.data.cassandra.contact-points}")
	private String contactPoints;
	
	@Value("${spring.data.cassandra.keyspace-name}")
	private String keySpace;
	
	@Override
	protected String getKeyspaceName() {
		return keySpace;
	}

	@Override
	public String[] getEntityBasePackages() {
		String[] retVal = {"info.glennengstrand.dao.cassandra"};
		return retVal;
	}

	@Override
	public SchemaAction getSchemaAction() {
		return SchemaAction.NONE;
	}

	@Override
	protected String getContactPoints() {
		return contactPoints;
	}

}
