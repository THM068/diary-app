package diaryapp.routes
import zio.{URLayer, ZLayer}
import zhttp.http._
import zio.json._
import diaryapp.services.ProfileService

final case class ProfileApp (profileService: ProfileService){

  def routes(): Http[Any, Throwable, Request, Response] = Http.collectZIO[Request] {
    case Method.GET -> !! / "profile" / id =>
      for {
        profile <- profileService.findProfile(id)
      } yield Response.json(profile.toJson)
  }
}
object ProfileApp {
  val layer: URLayer[ProfileService, ProfileApp] = ZLayer.fromFunction(ProfileApp.apply _)
}
