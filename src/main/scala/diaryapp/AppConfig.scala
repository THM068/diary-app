package diaryapp

import zio.config.magnolia.deriveConfig
import zio.{Config, ZIO, ZLayer}
case class AppConfig(port: Int, faunaKey: String, endpoint: String, jwtSecret: String)
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




