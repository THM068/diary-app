package diaryapp.routes

import zio.{URLayer, ZLayer}
import zhttp.http._
import zio.json._

class HealthCheckApp {
  import AppStatus._

  def routes(): Http[Any, Throwable, Request, Response] = Http.collect[Request] {
    case Method.GET -> !! / "health" / "up" =>
      Response.json(AppStatus("UP").toJson)
  }

}

case class AppStatus(status: String)

object AppStatus {
  implicit val decoder: JsonDecoder[AppStatus] = DeriveJsonDecoder.gen[AppStatus]
  implicit val encoder: JsonEncoder[AppStatus] = DeriveJsonEncoder.gen[AppStatus]
}

object HealthCheckApp {
  val layer = ZLayer.succeed(new HealthCheckApp())
}
