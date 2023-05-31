package diaryapp

import diaryapp.persistence.{AccountRepository, FaunaDatabaseClient, FaunaSettings, ProfileRepository, StoryRepository}
import diaryapp.routes.{AuthenticationApp, DiaryAppServer, HealthCheckApp, ProfileApp, StoryApp}
import diaryapp.services.{AuthenticationService, JWTAuthenticationService, ProfileService, StoryService}
import zio._
object Main extends ZIOAppDefault {

  override val run: Task[Unit] =
    ZIO
      .serviceWithZIO[DiaryAppServer](_.runServer)
      .provide(DiaryAppServer.layer,
      HealthCheckApp.layer,
      ProfileApp.layer,
      StoryApp.layer,
      ProfileService.layer,
      StoryService.layer,
      ProfileRepository.layer,
      StoryRepository.layer,
      FaunaDatabaseClient.layer,
      FaunaSettings.layer,
      AuthenticationApp.layer,
      AuthenticationService.layer,
      JWTAuthenticationService.layer,
      AccountRepository.layer,
      FaunaConfig.layer, ZLayer.Debug.mermaid)
}