package info.glennengstrand.io

import java.sql.Connection

import com.mchange.v2.c3p0.ComboPooledDataSource

/** helper function that wraps access to c3p0 */
object PooledRelationalDataStore {

  private def getPooledDataSource: ComboPooledDataSource = {
    val ds = new ComboPooledDataSource
    ds.setDriverClass(IO.settings.getProperty(IO.jdbcDriveName))
    ds.setJdbcUrl(IO.settings.getProperty(IO.jdbcUrl))
    ds.setUser(IO.settings.getProperty(IO.jdbcUser))
    ds.setPassword(IO.settings.getProperty(IO.jdbcPassword))
    ds.setMinPoolSize(IO.settings.getProperty(IO.jdbcMinPoolSize).toInt)
    ds.setAcquireIncrement(IO.settings.getProperty(IO.jdbcMinPoolSize).toInt)
    ds.setMaxPoolSize(IO.settings.getProperty(IO.jdbcMaxPoolSize).toInt)
    ds.setMaxStatements(IO.settings.getProperty(IO.jdbcMaxStatements).toInt)
    ds
  }
  lazy val ds: ComboPooledDataSource = getPooledDataSource

}

/** responsible for setting up JDBC connection pools */
trait PooledRelationalDataStore {
  def getDbConnection: Connection = {
    PooledRelationalDataStore.ds.getConnection
  }
}
