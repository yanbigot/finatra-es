package com.yb.repository

import com.sksamuel.elastic4s.http.{ElasticClient, ElasticProperties}

object EsRepository {
  val client = ElasticClient(ElasticProperties("http://localhost:9200"))
}
