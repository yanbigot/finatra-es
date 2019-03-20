package com.yb

import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.routing.HttpRouter
import com.yb.controller.EsController

object App extends FinEsServer

class FinEsServer extends HttpServer {
  override protected def configureHttp(router: HttpRouter): Unit = {
    router.add[EsController]
  }
  override val defaultHttpsPort: String = ":8899"
}