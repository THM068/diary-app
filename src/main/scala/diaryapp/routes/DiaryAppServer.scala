package diaryapp.routes

import diaryapp.AppConfig
import zhttp.http
import zhttp.http._
import zhttp.service.Server
import zio.{ZIO, ZLayer}


final case class DiaryAppServer(
          healthCheckApp: HealthCheckApp,
          profileApp: ProfileApp,
          storyApp: StoryApp,
          authenticationApp: AuthenticationApp) {

  val applications: Http[Any, Throwable, Request, Response] = healthCheckApp.routes ++ authenticationApp.routes  ++ storyApp.routes ++ profileApp.routes   @@ Middleware.debug

  def runServer(): ZIO[Any, Throwable, Unit] = for {
    appConfig <- ZIO.config[AppConfig](AppConfig.config)
    port = appConfig.port
    _ <- ZIO.debug(s"Starting server on http://localhost:${port}")
    _ <- Server.start(port= appConfig.port, http = applications)
  } yield ()

}
object DiaryAppServer {

  val layer: ZLayer[HealthCheckApp with ProfileApp with StoryApp with AuthenticationApp, Nothing, DiaryAppServer] =
    ZLayer.fromFunction(DiaryAppServer.apply _)
}