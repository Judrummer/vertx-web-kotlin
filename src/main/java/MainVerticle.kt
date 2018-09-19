package io.vertx.example

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

@Suppress("unused")
class MainVerticle : AbstractVerticle() {

  override fun start(startFuture: Future<Void>) {
    val router = createRouter()

    vertx.createHttpServer()
        .requestHandler { router.accept(it) }
        .listen(config().getInteger("http.port", 8080)) { result ->
          if (result.succeeded()) {
            startFuture.complete()
          } else {
            startFuture.fail(result.cause())
          }
        }
  }

  private fun createRouter() = Router.router(vertx).apply {
    get("/").handler {
      req -> req.response().end("Hello")
    }
    get("/people").handler(handlerPeople)
  }

  //
  // Handlers

  data class Person(val firstname: String, val lastname: String)

  val handlerPeople = Handler<RoutingContext> { req ->
    req.response().endWithJson(listOf(
        Person("Noctis", "Caelum"),
        Person("Lunafreya", "Fleuret")
    ))
  }



  //
  // Utilities

  /**
   * Extension to the HTTP response to output JSON objects.
   */
  fun HttpServerResponse.endWithJson(obj: Any) {
    this.putHeader("Content-Type", "application/json; charset=utf-8").end(Json.encodePrettily(obj))
  }
}