package com.yb.service

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.http.Response
import com.sksamuel.elastic4s.http.search.SearchResponse
import com.sksamuel.elastic4s.searches.queries.BoolQuery
import com.yb.repository.EsRepository.client
import org.joda.time.DateTime

case class Params(reqParams: Map[String, String])
case class DictAttribute(name: String, weight: Int)
case class Plan(id: String, stages: Seq[Stage])
case class Stage(name: String, tasks: Seq[Task])
case class Task(name: String, entity: String)

trait TService{
  val ENTRY_POINT_PAGE_SIZE = 100
  val OFFSET = "OFFSET"
  type Rows = Seq[Map[String, AnyRef]]
  type Row  = Map[String, AnyRef]

  def process(p: Params): SearchResponse
  def fromRequestParamsGetEntryPoint(): DictAttribute
  def fromEntryPointGetExecutionPlan(): Plan
  def fromRequestParamsGetPagination(p: Params): Int
  def fromEntryPointRowsGetChildren(rows: Rows): Map[String, Rows]
}
abstract class Service extends TService {
  import com.sksamuel.elastic4s.http.ElasticDsl._

  override def process(p: Params):SearchResponse = {
    //compute from clause
    val fromClause = fromRequestParamsGetPagination(p)
    val nextFrom   = fromClause + ENTRY_POINT_PAGE_SIZE

    //fetch entry point
    val response: Response[SearchResponse] = client.execute {
      search("shakespeare").from(fromClause).size(ENTRY_POINT_PAGE_SIZE) bool{
        must (
          termsQuery("play_name", Seq("Henry IV", "Hamlet"))
        )
      }
    }.await
    //result to rows
    val entryPointRows = response.result.hits.hits.map(row => row.fields).toSeq

    //fetch next stage with children from entry point
    val children = fromEntryPointRowsGetChildren(entryPointRows)

    //nest entry point and children
    val nestedResult = ???

    //return
    response.result
  }

  override def fromRequestParamsGetPagination(p: Params): Int = {
    p.reqParams.keySet.contains(OFFSET) match {
      case true => p.reqParams(OFFSET).toInt
      case false => 0
    }
  }

}
