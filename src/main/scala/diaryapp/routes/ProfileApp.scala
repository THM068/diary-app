package diaryapp.routes
import diaryapp.persistence.model.Profile._
import diaryapp.persistence.model.{Message, Profile, ReplaceProfileData}
import diaryapp.routes.ServerUtils.parseBody
import diaryapp.services.ProfileService
import zhttp.http._
import zio.json._
import zio.{URLayer, ZIO, ZLayer}

final case class ProfileApp (profileService: ProfileService){

  def routes(): Http[Any, Throwable, Request, Response] = Http.collectZIO[Request] {
    case Method.GET -> !! / "profile" / id =>
      (for {
        profile <- profileService.findProfile(id)
      } yield {
        profile match {
          case Some(p) =>
            Response.json(p.toJson)
          case _ =>
            Response.json(
              Message("Profile does not exist", "profile-service").toJson)
              .setStatus(Status.NotFound)
        }
      }).catchSome {
        case e: Exception =>
          ZIO.succeed(
            Response.json(
              Message("An Unexpected error occurred", "profile-service").toJson)
              .setStatus(Status.InternalServerError)
          )
      }
    case req @ Method.POST -> !! / "profile" =>
      (for {
        profileData <- parseBody[ReplaceProfileData](req)
        nextId <- profileService.nextId()
        profile = Profile(id=nextId, name = profileData.name)
        savedProfile <- profileService.save(profile)
      } yield Response.json(savedProfile.toJson)).catchSome {
        case e: Exception =>
          ZIO.succeed(
            Response.json(
              Message(e.getMessage, "profile-service").toJson)
              .setStatus(Status.BadRequest)
          )
        case AppError.MissingBodyError =>
          ZIO.succeed(
            Response.json(
              Message("Missing body in request", "profile-service").toJson)
              .setStatus(Status.BadRequest)
          )
        case AppError.JsonDecodingError(message) =>
          ZIO.succeed(
            Response.json(
              Message(message, "profile-service").toJson)
              .setStatus(Status.BadRequest)
          )
      }
  }
}
object ProfileApp {
  val layer: URLayer[ProfileService, ProfileApp] = ZLayer.fromFunction(ProfileApp.apply _)
}
