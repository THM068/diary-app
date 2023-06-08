package diaryapp.routes

import diaryapp.persistence.model.Message
import diaryapp.routes.ServerUtils.parseBody
import zio._
import zhttp.http._
import diaryapp.services.{AuthenticationService, JWTAuthenticationService, LoginCredentials, UserDetails}
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, EncoderOps, JsonDecoder, JsonEncoder}

case class JwtToken(token: String)
object JwtToken {
  implicit val decoder: JsonDecoder[JwtToken] = DeriveJsonDecoder.gen[JwtToken]
  implicit val encoder: JsonEncoder[JwtToken] = DeriveJsonEncoder.gen[JwtToken]
}
case class AuthenticationApp(authenticationService: AuthenticationService, jwtAuthenticationService: JWTAuthenticationService) {
 import JwtToken._
  def routes(): Http[Any, Throwable, Request, Response] = Http.collectZIO[Request] {
    case req @ Method.POST -> !! / "login" =>
      (for {
        loginCredentials <- parseBody[LoginCredentials](req)
        loginResponse <- authenticationService.login(loginCredentials)
        jwtResult  =  jwtAuthenticationService.jwtEncode(UserDetails(profileId = loginResponse.profileId, name = loginResponse.name))
      } yield Response.json(JwtToken(token = jwtResult).toJson)).catchSome {
        case e: Exception =>
          ZIO.logError(e.getMessage)
          val message = Message(message = "Login failed", service = "authentication-service")
          ZIO.succeed(Response.json(message.toJson))
      }

  }
}

object AuthenticationApp {
  val layer: URLayer[AuthenticationService with JWTAuthenticationService, AuthenticationApp] = ZLayer.fromFunction(AuthenticationApp.apply _)
}
