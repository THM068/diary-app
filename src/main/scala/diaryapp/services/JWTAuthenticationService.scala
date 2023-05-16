package diaryapp.services

import diaryapp.AppConfig
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import zio._
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, EncoderOps, JsonDecoder, JsonEncoder}

import java.time.Clock

case class UserDetails(name: String, profileId: String)

object UserDetails {
  implicit val decode:JsonDecoder[UserDetails] = DeriveJsonDecoder.gen[UserDetails]
  implicit val encode:JsonEncoder[UserDetails] = DeriveJsonEncoder.gen[UserDetails]
}
trait JWTAuthenticationService {
  def jwtDecode(token: String): Option[JwtClaim]

  def jwtEncode(userDetails: UserDetails): String
}

final case class JWTAuthenticationServiceLive(jwtSecret: String) extends JWTAuthenticationService {
  implicit val clock: Clock = Clock.systemUTC
  override def jwtDecode(token: String): Option[JwtClaim] = Jwt.decode(token, jwtSecret, Seq(JwtAlgorithm.HS512)).toOption

  override def jwtEncode(userDetails: UserDetails): String = {
    import UserDetails._
    val claim = JwtClaim {
      userDetails.toJson
    }.issuedNow.expiresIn(604800)
    Jwt.encode(claim, jwtSecret, JwtAlgorithm.HS512)
  }
}

object JWTAuthenticationService {
  val layer: ZLayer[Any, Config.Error, JWTAuthenticationService] =
    ZLayer
      .fromZIO(
        ZIO.config[AppConfig](AppConfig.config).map { appConfig =>
          JWTAuthenticationServiceLive(appConfig.jwtSecret)
        }
      )

}
