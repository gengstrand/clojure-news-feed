package info.glennengstrand.news

import java.io.{InputStream, Reader}
import java.math.BigDecimal
import java.net.URL
import java.sql._
import java.util.Calendar

/**
 * Created by glenn on 5/2/15.
 */
class MockPreparedStatement extends PreparedStatement {
  override def executeQuery(): ResultSet = { new MockResultSet }

  override def setByte(parameterIndex: Int, x: Byte): Unit = {}

  override def getParameterMetaData: ParameterMetaData = { null }

  override def setRef(parameterIndex: Int, x: Ref): Unit = {}

  override def clearParameters(): Unit = {}

  def setBytes(parameterIndex: Int, x: scala.Array[Byte]): Unit = {}

  override def setBinaryStream(parameterIndex: Int, x: InputStream, length: Int): Unit = {}

  override def setBinaryStream(parameterIndex: Int, x: InputStream, length: Long): Unit = {}

  override def setBinaryStream(parameterIndex: Int, x: InputStream): Unit = {}

  override def setAsciiStream(parameterIndex: Int, x: InputStream, length: Int): Unit = {}

  override def setAsciiStream(parameterIndex: Int, x: InputStream, length: Long): Unit = {}

  override def setAsciiStream(parameterIndex: Int, x: InputStream): Unit = {}

  override def setObject(parameterIndex: Int, x: scala.Any, targetSqlType: Int): Unit = {}

  override def setObject(parameterIndex: Int, x: scala.Any): Unit = {}

  override def setObject(parameterIndex: Int, x: scala.Any, targetSqlType: Int, scaleOrLength: Int): Unit = {}

  override def setDate(parameterIndex: Int, x: Date): Unit = {}

  override def setDate(parameterIndex: Int, x: Date, cal: Calendar): Unit = {}

  override def setTimestamp(parameterIndex: Int, x: Timestamp): Unit = {}

  override def setTimestamp(parameterIndex: Int, x: Timestamp, cal: Calendar): Unit = {}

  override def setUnicodeStream(parameterIndex: Int, x: InputStream, length: Int): Unit = {}

  override def getMetaData: ResultSetMetaData = { null }

  override def setBlob(parameterIndex: Int, x: Blob): Unit = {}

  override def setBlob(parameterIndex: Int, inputStream: InputStream, length: Long): Unit = {}

  override def setBlob(parameterIndex: Int, inputStream: InputStream): Unit = {}

  override def addBatch(): Unit = {}

  override def execute(): Boolean = { false }

  override def setNClob(parameterIndex: Int, value: NClob): Unit = {}

  override def setNClob(parameterIndex: Int, reader: Reader, length: Long): Unit = {}

  override def setNClob(parameterIndex: Int, reader: Reader): Unit = {}

  override def setArray(parameterIndex: Int, x: Array): Unit = {}

  override def setNCharacterStream(parameterIndex: Int, value: Reader, length: Long): Unit = {}

  override def setNCharacterStream(parameterIndex: Int, value: Reader): Unit = {}

  override def setURL(parameterIndex: Int, x: URL): Unit = {}

  override def setRowId(parameterIndex: Int, x: RowId): Unit = {}

  override def setSQLXML(parameterIndex: Int, xmlObject: SQLXML): Unit = {}

  override def setString(parameterIndex: Int, x: String): Unit = {}

  override def setFloat(parameterIndex: Int, x: Float): Unit = {}

  override def setNString(parameterIndex: Int, value: String): Unit = {}

  override def setBoolean(parameterIndex: Int, x: Boolean): Unit = {}

  override def setDouble(parameterIndex: Int, x: Double): Unit = {}

  override def setBigDecimal(parameterIndex: Int, x: BigDecimal): Unit = {}

  override def executeUpdate(): Int = { 0 }

  override def setTime(parameterIndex: Int, x: Time): Unit = {}

  override def setTime(parameterIndex: Int, x: Time, cal: Calendar): Unit = {}

  override def setShort(parameterIndex: Int, x: Short): Unit = {}

  override def setLong(parameterIndex: Int, x: Long): Unit = {}

  override def setCharacterStream(parameterIndex: Int, reader: Reader, length: Int): Unit = {}

  override def setCharacterStream(parameterIndex: Int, reader: Reader, length: Long): Unit = {}

  override def setCharacterStream(parameterIndex: Int, reader: Reader): Unit = {}

  override def setClob(parameterIndex: Int, x: Clob): Unit = {}

  override def setClob(parameterIndex: Int, reader: Reader, length: Long): Unit = {}

  override def setClob(parameterIndex: Int, reader: Reader): Unit = {}

  override def setNull(parameterIndex: Int, sqlType: Int): Unit = {}

  override def setNull(parameterIndex: Int, sqlType: Int, typeName: String): Unit = {}

  override def setInt(parameterIndex: Int, x: Int): Unit = {}

  override def setMaxFieldSize(max: Int): Unit = {}

  override def getMoreResults: Boolean = { false }

  override def getMoreResults(current: Int): Boolean = { false }

  override def clearWarnings(): Unit = {}

  override def getGeneratedKeys: ResultSet = { null }

  override def cancel(): Unit = {}

  override def getResultSet: ResultSet = { null }

  override def setPoolable(poolable: Boolean): Unit = {}

  override def isPoolable: Boolean = { false }

  override def setCursorName(name: String): Unit = {}

  override def getUpdateCount: Int = { 0 }

  override def addBatch(sql: String): Unit = {}

  override def getMaxRows: Int = { 0 }

  override def execute(sql: String): Boolean = { false }

  override def execute(sql: String, autoGeneratedKeys: Int): Boolean = { false }

  override def execute(sql: String, columnIndexes: scala.Array[Int]): Boolean = { false }

  override def execute(sql: String, columnNames: scala.Array[String]): Boolean = { false }

  override def executeQuery(sql: String): ResultSet = { null }

  override def getResultSetType: Int = { 0 }

  override def setMaxRows(max: Int): Unit = {}

  override def getFetchSize: Int = { 0 }

  override def getResultSetHoldability: Int = { 0 }

  override def setFetchDirection(direction: Int): Unit = {}

  override def getFetchDirection: Int = { 0 }

  override def getResultSetConcurrency: Int = { 0 }

  override def clearBatch(): Unit = {}

  override def close(): Unit = {}

  override def isClosed: Boolean = { false }

  override def closeOnCompletion(): Unit = {}

  override def isCloseOnCompletion(): Boolean = { false }

  override def executeUpdate(sql: String): Int = { 0 }

  override def executeUpdate(sql: String, autoGeneratedKeys: Int): Int = { 0 }

  override def executeUpdate(sql: String, columnIndexes: scala.Array[Int]): Int = { 0 }

  override def executeUpdate(sql: String, columnNames: scala.Array[String]): Int = { 0 }

  override def getQueryTimeout: Int = { 0 }

  override def getWarnings: SQLWarning = { null }

  override def setFetchSize(rows: Int): Unit = {}

  override def setQueryTimeout(seconds: Int): Unit = {}

  override def executeBatch(): scala.Array[Int] = { null }

  override def setEscapeProcessing(enable: Boolean): Unit = {}

  override def getConnection: Connection = { null }

  override def getMaxFieldSize: Int = { 0 }

  override def unwrap[T](iface: Class[T]): T = { throw new SQLException }

  override def isWrapperFor(iface: Class[_]): Boolean = { false }
}
