package diaryapp.routes
import diaryapp.persistence.model.StoryData
import diaryapp.routes.ServerUtils.parseBody
import zio._
import zhttp.http._
import diaryapp.services.{AuthenticationService, JWTAuthenticationService, LoginCredentials, UserDetails}

case class AuthenticationApp(authenticationService: AuthenticationService, jwtAuthenticationService: JWTAuthenticationService) {

  def routes(): Http[Any, Throwable, Request, Response] = Http.collectZIO[Request] {
    case req @ Method.POST -> !! / "login" =>
      for {
        loginCredentials <- parseBody[LoginCredentials](req)
        loginResponse <- authenticationService.login(loginCredentials)
        jwtResult  =  jwtAuthenticationService.jwtEncode(UserDetails(profileId = loginResponse.profileId, name = loginResponse.name))
      } yield Response.text("aaa " + jwtResult)

  }
}

object AuthenticationApp {
  val layer: URLayer[AuthenticationService with JWTAuthenticationService, AuthenticationApp] = ZLayer.fromFunction(AuthenticationApp.apply _)
}
