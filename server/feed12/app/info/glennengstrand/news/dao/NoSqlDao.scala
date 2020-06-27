package info.glennengstrand.news.dao

import com.datastax.oss.driver.api.core.CqlSession
import java.net.InetSocketAddress

trait NoSqlDao {
  def connect(f: CqlSession => Unit): Unit
}

class NoSqlDaoImpl extends NoSqlDao {
  val noSqlHost = sys.env.get("NOSQL_HOST").getOrElse("localhost")
  val noSqlTtl = sys.env.get("NOSQL_TTL").getOrElse(1000 * 60 * 60 * 24)
  val noSqlKeyspace = sys.env.get("NOSQL_KEYSPACE").getOrElse("activity")
  override def connect(f: CqlSession => Unit): Unit = {
      f(CqlSession.builder()
      .addContactPoint(new InetSocketAddress(noSqlHost, 9042))
      .withLocalDatacenter("datacenter1")
      .withKeyspace(noSqlKeyspace)
      .build)
  }
}

class MockNoSqlDaoImpl extends NoSqlDao {
  override def connect(f: CqlSession => Unit): Unit = {
    
  }
}
