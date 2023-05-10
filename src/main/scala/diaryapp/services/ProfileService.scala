package diaryapp.services
import zio._
import diaryapp.persistence.ProfileRepository
import diaryapp.persistence.model.Profile

case class ProfileService(profileRepository: ProfileRepository) {

  def findProfile(id: String): Task[Option[Profile]] = {
    ZIO.fromFuture { implicit ec =>
      profileRepository.find(id)
    }
  }

}

object ProfileService {
  val layer: URLayer[ProfileRepository, ProfileService] =  ZLayer.fromFunction(ProfileService.apply _)
}
