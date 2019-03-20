package com.yb.controller

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.yb.service.{DictAttribute, Params, Plan, Service}

class EsController extends Controller {

  val service = new Service {
    override def fromRequestParamsGetEntryPoint(): DictAttribute = ???

    override def fromEntryPointGetExecutionPlan(): Plan = ???
  }

  get("/es") { request: Request =>
    val params = Params(Map())
    val result = service.process(params)
    result
  }
}
