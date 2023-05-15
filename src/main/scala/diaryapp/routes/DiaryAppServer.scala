package diaryapp.routes

import diaryapp.AppConfig
import zhttp.http
import zhttp.http._
import zhttp.service.Server
import zio.{ZIO, ZLayer}


final case class DiaryAppServer(
          healthCheckApp: HealthCheckApp,
          profileApp: ProfileApp,
          storyApp: StoryApp) {

  val applications: Http[Any, Throwable, Request, Response] =healthCheckApp.routes ++ profileApp.routes ++ storyApp.routes @@ Middleware.debug

  def runServer(): ZIO[Any, Throwable, Unit] = for {
    appConfig <- ZIO.config[AppConfig](AppConfig.config)
    port = appConfig.port
    _ <- ZIO.debug(s"Starting server on http://localhost:${port}")
    _ <- Server.start(port= appConfig.port, http = applications)
  } yield ()

}
object DiaryAppServer {

  val layer: ZLayer[HealthCheckApp with ProfileApp with StoryApp, Nothing, DiaryAppServer] =
    ZLayer.fromFunction(DiaryAppServer.apply _)
}