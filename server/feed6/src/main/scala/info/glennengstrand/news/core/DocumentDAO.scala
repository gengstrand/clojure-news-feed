package info.glennengstrand.news.core

case class DocumentIdentity(docIndex: String, docType: String, docId: String, docTerm: String, docResult: String)

trait DocumentDAO[D <: AnyRef] {
  def index(doc: D): Unit
  def search(keywords: String): List[Int]
  def identity: DocumentIdentity
  def source(doc: D, key: String): Map[String, Object] = Map()
}

