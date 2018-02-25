package info.glennengstrand.news.core

import org.json4s.jackson.Serialization.write
import org.json4s._
import org.json4s.jackson.Serialization

case class DocumentIdentity(docIndex: String, docType: String, docId: String, docTerm: String, docResult: String)

trait DocumentDAO[D <: AnyRef] {
  val formats = Serialization.formats(NoTypeHints)
  def index(doc: D): Unit
  def search(keywords: String): List[Int]
  def identity: DocumentIdentity
  def source(doc: D, key: String): String = write(doc)(formats)
}

