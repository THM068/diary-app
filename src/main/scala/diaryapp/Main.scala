package diaryapp

import diaryapp.persistence.{FaunaDatabaseClient, FaunaSettings, ProfileRepository}
import diaryapp.routes.{DiaryAppServer, HealthCheckApp, ProfileApp}
import diaryapp.services.ProfileService
import zio._

object Main extends ZIOAppDefault {

  override val run: Task[Unit] =
    ZIO
      .serviceWithZIO[DiaryAppServer](_.runServer)
      .provide(DiaryAppServer.layer,
      HealthCheckApp.layer,
      ProfileApp.layer,
      ProfileService.layer,
      ProfileRepository.layer,
      FaunaDatabaseClient.layer,
      FaunaSettings.layer,
      FaunaConfig.layer)


}