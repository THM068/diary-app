package diaryapp

import zio.{Config, ZIO, ZLayer}
import zio.config.magnolia.deriveConfig
case class AppConfig(port: Int, faunaKey: String, endpoint: String)
case class FaunaConfig(faunaKey: String, endpoint: String)

object AppConfig {
  val config: Config[AppConfig] = deriveConfig[AppConfig].nested("diaryapp")
}

object FaunaConfig {
  val layer: ZLayer[Any, Config.Error, FaunaConfig] =
    ZLayer
      .fromZIO(
        ZIO.config[AppConfig](AppConfig.config).map { appConfig =>
          FaunaConfig(
            faunaKey = appConfig.faunaKey,
            endpoint = appConfig.endpoint
          )
        }
      )
}




