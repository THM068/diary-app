package diaryapp.routes
import diaryapp.persistence.model.{Profile, ReplaceProfileData}
import diaryapp.persistence.model.Profile._
import diaryapp.routes.ServerUtils.parseBody
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
    case req @ Method.POST -> !! / "profile" =>
      for {
        profileData <- parseBody[ReplaceProfileData](req)
        nextId <- profileService.nextId()
        profile = Profile(id=nextId, name = profileData.name)
        savedProfile <- profileService.save(profile)
      } yield Response.json(savedProfile.toJson)
  }
}
object ProfileApp {
  val layer: URLayer[ProfileService, ProfileApp] = ZLayer.fromFunction(ProfileApp.apply _)
}
