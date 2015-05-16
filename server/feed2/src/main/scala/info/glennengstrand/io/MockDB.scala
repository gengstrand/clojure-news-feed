package info.glennengstrand.io

import java.io.{InputStream, Reader}
import java.math.BigDecimal
import java.net.URL
import java.sql._
import java.util
import java.util.Calendar

/** mock result set for unit testing only */
class MockResultSet extends java.sql.ResultSet {
  override def next(): Boolean = { false }

  override def getType: Int = { 0 }

  override def isBeforeFirst: Boolean = { false }

  override def updateString(columnIndex: Int, x: String): Unit = { }

  override def updateString(columnLabel: String, x: String): Unit = { }

  override def getTimestamp(columnIndex: Int): Timestamp = { null }

  override def getTimestamp(columnLabel: String): Timestamp = { null }

  override def getTimestamp(columnIndex: Int, cal: Calendar): Timestamp = { null }

  override def getTimestamp(columnLabel: String, cal: Calendar): Timestamp = { null }

  override def updateNString(columnIndex: Int, nString: String): Unit = { }

  override def updateNString(columnLabel: String, nString: String): Unit = { }

  override def clearWarnings(): Unit = { }

  override def updateTimestamp(columnIndex: Int, x: Timestamp): Unit = { }

  override def updateTimestamp(columnLabel: String, x: Timestamp): Unit = { }

  override def updateByte(columnIndex: Int, x: Byte): Unit = { }

  override def updateByte(columnLabel: String, x: Byte): Unit = { }

  override def updateBigDecimal(columnIndex: Int, x: BigDecimal): Unit = { }

  override def updateBigDecimal(columnLabel: String, x: BigDecimal): Unit = { }

  override def updateDouble(columnIndex: Int, x: Double): Unit = { }

  override def updateDouble(columnLabel: String, x: Double): Unit = { }

  override def updateDate(columnIndex: Int, x: Date): Unit = { }

  override def updateDate(columnLabel: String, x: Date): Unit = { }

  override def isAfterLast: Boolean = { false }

  override def updateBoolean(columnIndex: Int, x: Boolean): Unit = { }

  override def updateBoolean(columnLabel: String, x: Boolean): Unit = { }

  override def getBinaryStream(columnIndex: Int): InputStream = { null }

  override def getBinaryStream(columnLabel: String): InputStream = { null }

  override def beforeFirst(): Unit = { }

  override def updateNCharacterStream(columnIndex: Int, x: Reader, length: Long): Unit = { }

  override def updateNCharacterStream(columnLabel: String, reader: Reader, length: Long): Unit = { }

  override def updateNCharacterStream(columnIndex: Int, x: Reader): Unit = { }

  override def updateNCharacterStream(columnLabel: String, reader: Reader): Unit = { }

  override def updateNClob(columnIndex: Int, nClob: NClob): Unit = { }

  override def updateNClob(columnLabel: String, nClob: NClob): Unit = { }

  override def updateNClob(columnIndex: Int, reader: Reader, length: Long): Unit = { }

  override def updateNClob(columnLabel: String, reader: Reader, length: Long): Unit = { }

  override def updateNClob(columnIndex: Int, reader: Reader): Unit = { }

  override def updateNClob(columnLabel: String, reader: Reader): Unit = { }

  override def last(): Boolean = { false }

  override def isLast: Boolean = { false }

  override def getNClob(columnIndex: Int): NClob = { null }

  override def getNClob(columnLabel: String): NClob = { null }

  override def getCharacterStream(columnIndex: Int): Reader = { null }

  override def getCharacterStream(columnLabel: String): Reader = { null }

  override def updateArray(columnIndex: Int, x: Array): Unit = { }

  override def updateArray(columnLabel: String, x: Array): Unit = { }

  override def updateBlob(columnIndex: Int, x: Blob): Unit = { }

  override def updateBlob(columnLabel: String, x: Blob): Unit = { }

  override def updateBlob(columnIndex: Int, inputStream: InputStream, length: Long): Unit = { }

  override def updateBlob(columnLabel: String, inputStream: InputStream, length: Long): Unit = { }

  override def updateBlob(columnIndex: Int, inputStream: InputStream): Unit = { }

  override def updateBlob(columnLabel: String, inputStream: InputStream): Unit = { }

  override def getDouble(columnIndex: Int): Double = { 0d }

  override def getDouble(columnLabel: String): Double = { 0d }

  override def getArray(columnIndex: Int): Array = { null }

  override def getArray(columnLabel: String): Array = { null }

  override def isFirst: Boolean = { false }

  override def getURL(columnIndex: Int): URL = { null }

  override def getURL(columnLabel: String): URL = { null }

  override def updateRow(): Unit = { }

  override def insertRow(): Unit = { }

  override def getMetaData: ResultSetMetaData = { null }

  override def updateBinaryStream(columnIndex: Int, x: InputStream, length: Int): Unit = { }

  override def updateBinaryStream(columnLabel: String, x: InputStream, length: Int): Unit = { }

  override def updateBinaryStream(columnIndex: Int, x: InputStream, length: Long): Unit = { }

  override def updateBinaryStream(columnLabel: String, x: InputStream, length: Long): Unit = { }

  override def updateBinaryStream(columnIndex: Int, x: InputStream): Unit = { }

  override def updateBinaryStream(columnLabel: String, x: InputStream): Unit = { }

  override def absolute(row: Int): Boolean = { false }

  override def updateRowId(columnIndex: Int, x: RowId): Unit = { }

  override def updateRowId(columnLabel: String, x: RowId): Unit = { }

  override def getRowId(columnIndex: Int): RowId = { null }

  override def getRowId(columnLabel: String): RowId = { null }

  override def moveToInsertRow(): Unit = { }

  override def rowInserted(): Boolean = { false }

  override def getFloat(columnIndex: Int): Float = { 0.0f }

  override def getFloat(columnLabel: String): Float = { 0.0f }

  override def getBigDecimal(columnIndex: Int, scale: Int): BigDecimal = { null }

  override def getBigDecimal(columnLabel: String, scale: Int): BigDecimal = { null }

  override def getBigDecimal(columnIndex: Int): BigDecimal = { null }

  override def getBigDecimal(columnLabel: String): BigDecimal = { null }

  override def getClob(columnIndex: Int): Clob = { null }

  override def getClob(columnLabel: String): Clob = { null }

  override def getRow: Int = { 0 }

  override def getLong(columnIndex: Int): Long = { 0l }

  override def getLong(columnLabel: String): Long = { 0l }

  override def getHoldability: Int = { 0 }

  override def updateFloat(columnIndex: Int, x: Float): Unit = { }

  override def updateFloat(columnLabel: String, x: Float): Unit = { }

  override def afterLast(): Unit = { }

  override def refreshRow(): Unit = { }

  override def getNString(columnIndex: Int): String = { null }

  override def getNString(columnLabel: String): String = { null }

  override def deleteRow(): Unit = { }

  override def getConcurrency: Int = { 0 }

  override def updateObject(columnIndex: Int, x: scala.Any, scaleOrLength: Int): Unit = { }

  override def updateObject(columnIndex: Int, x: scala.Any): Unit = { }

  override def updateObject(columnLabel: String, x: scala.Any, scaleOrLength: Int): Unit = { }

  override def updateObject(columnLabel: String, x: scala.Any): Unit = { }

  override def getFetchSize: Int = { 0 }

  override def getTime(columnIndex: Int): Time = { null }

  override def getTime(columnLabel: String): Time = { null }

  override def getTime(columnIndex: Int, cal: Calendar): Time = { null }

  override def getTime(columnLabel: String, cal: Calendar): Time = { null }

  override def updateCharacterStream(columnIndex: Int, x: Reader, length: Int): Unit = { }

  override def updateCharacterStream(columnLabel: String, reader: Reader, length: Int): Unit = { }

  override def updateCharacterStream(columnIndex: Int, x: Reader, length: Long): Unit = { }

  override def updateCharacterStream(columnLabel: String, reader: Reader, length: Long): Unit = { }

  override def updateCharacterStream(columnIndex: Int, x: Reader): Unit = { }

  override def updateCharacterStream(columnLabel: String, reader: Reader): Unit = { }

  override def getByte(columnIndex: Int): Byte = { 0x00b }

  override def getByte(columnLabel: String): Byte = { 0x00b }

  override def getBoolean(columnIndex: Int): Boolean = { false }

  override def getBoolean(columnLabel: String): Boolean = { false }

  override def setFetchDirection(direction: Int): Unit = { }

  override def getFetchDirection: Int = { 0 }

  override def updateRef(columnIndex: Int, x: Ref): Unit = { }

  override def updateRef(columnLabel: String, x: Ref): Unit = { }

  override def getAsciiStream(columnIndex: Int): InputStream = { null }

  override def getAsciiStream(columnLabel: String): InputStream = { null }

  override def getShort(columnIndex: Int): Short = { 0 }

  override def getShort(columnLabel: String): Short = { 0 }

  override def getObject(columnIndex: Int): AnyRef = { null }

  override def getObject(columnLabel: String): AnyRef = { null }

  override def getObject(columnIndex: Int, map: util.Map[String, Class[_]]): AnyRef = { null }

  override def getObject(columnLabel: String, map: util.Map[String, Class[_]]): AnyRef = { null }

  override def updateShort(columnIndex: Int, x: Short): Unit = { }

  override def updateShort(columnLabel: String, x: Short): Unit = { }

  override def getNCharacterStream(columnIndex: Int): Reader = { null }

  override def getNCharacterStream(columnLabel: String): Reader = { null }

  override def close(): Unit = { }

  override def relative(rows: Int): Boolean = { false }

  override def updateInt(columnIndex: Int, x: Int): Unit = { }

  override def updateInt(columnLabel: String, x: Int): Unit = { }

  override def wasNull(): Boolean = { false }

  override def rowUpdated(): Boolean = { false }

  override def getRef(columnIndex: Int): Ref = { null }

  override def getRef(columnLabel: String): Ref = { null }

  override def updateLong(columnIndex: Int, x: Long): Unit = { }

  override def updateLong(columnLabel: String, x: Long): Unit = { }

  override def moveToCurrentRow(): Unit = { }

  override def isClosed: Boolean = { false }

  override def updateClob(columnIndex: Int, x: Clob): Unit = { }

  override def updateClob(columnLabel: String, x: Clob): Unit = { }

  override def updateClob(columnIndex: Int, reader: Reader, length: Long): Unit = { }

  override def updateClob(columnLabel: String, reader: Reader, length: Long): Unit = { }

  override def updateClob(columnIndex: Int, reader: Reader): Unit = { }

  override def updateClob(columnLabel: String, reader: Reader): Unit = { }

  override def findColumn(columnLabel: String): Int = { 0 }

  override def getWarnings: SQLWarning = { null }

  override def getDate(columnIndex: Int): Date = { null }

  override def getDate(columnLabel: String): Date = { null }

  override def getDate(columnIndex: Int, cal: Calendar): Date = { null }

  override def getDate(columnLabel: String, cal: Calendar): Date = { null }

  override def getCursorName: String = { null }

  override def updateNull(columnIndex: Int): Unit = { }

  override def updateNull(columnLabel: String): Unit = { }

  override def getStatement: Statement = { null }

  override def cancelRowUpdates(): Unit = { }

  override def getSQLXML(columnIndex: Int): SQLXML = { null }

  override def getSQLXML(columnLabel: String): SQLXML = { null }

  override def getUnicodeStream(columnIndex: Int): InputStream = { null }

  override def getUnicodeStream(columnLabel: String): InputStream = { null }

  override def getInt(columnIndex: Int): Int = { 0 }

  override def getInt(columnLabel: String): Int = { 0 }

  override def updateTime(columnIndex: Int, x: Time): Unit = { }

  override def updateTime(columnLabel: String, x: Time): Unit = { }

  override def setFetchSize(rows: Int): Unit = { }

  override def previous(): Boolean = { false }

  override def updateAsciiStream(columnIndex: Int, x: InputStream, length: Int): Unit = { }

  override def updateAsciiStream(columnLabel: String, x: InputStream, length: Int): Unit = { }

  override def updateAsciiStream(columnIndex: Int, x: InputStream, length: Long): Unit = { }

  override def updateAsciiStream(columnLabel: String, x: InputStream, length: Long): Unit = { }

  override def updateAsciiStream(columnIndex: Int, x: InputStream): Unit = { }

  override def updateAsciiStream(columnLabel: String, x: InputStream): Unit = { }

  override def rowDeleted(): Boolean = { false }

  override def getBlob(columnIndex: Int): Blob = { null }

  override def getBlob(columnLabel: String): Blob = { null }

  override def first(): Boolean = { false }

  override def getBytes(columnIndex: Int): scala.Array[Byte] = { null }

  override def getBytes(columnLabel: String): scala.Array[Byte] = { null }

  override def updateBytes(columnIndex: Int, x: scala.Array[Byte]): Unit = { }

  override def updateBytes(columnLabel: String, x: scala.Array[Byte]): Unit = { }

  override def updateSQLXML(columnIndex: Int, xmlObject: SQLXML): Unit = { }

  override def updateSQLXML(columnLabel: String, xmlObject: SQLXML): Unit = { }

  override def getString(columnIndex: Int): String = { null }

  override def getString(columnLabel: String): String = { null }

  override def unwrap[T](iface: Class[T]): T = { throw new SQLException }

  override def isWrapperFor(iface: Class[_]): Boolean = { false }

  override def getObject[T](x$1: String,x$2: Class[T]): T = { throw new SQLException }

  override def getObject[T](x$1: Int,x$2: Class[T]): T = { throw new SQLException }
}

/** mock prepared statement creates a mock result set */
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
