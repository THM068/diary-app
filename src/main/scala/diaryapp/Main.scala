package diaryapp

import diaryapp.persistence.{FaunaDatabaseClient, FaunaSettings, ProfileRepository, StoryRepository}
import diaryapp.routes.{DiaryAppServer, HealthCheckApp, ProfileApp, StoryApp}
import diaryapp.services.{ProfileService, StoryService}
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
      FaunaConfig.layer, ZLayer.Debug.mermaid)


}