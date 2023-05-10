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

  def save(profile: Profile): Task[Profile] = {
    ZIO.fromFuture { implicit ex =>
      profileRepository.save(profile)
    }
  }

  def nextId(): Task[String] = {
    ZIO.fromFuture { implicit  ec =>
      profileRepository.nextId()
    }
  }

}

object ProfileService {
  val layer: URLayer[ProfileRepository, ProfileService] =  ZLayer.fromFunction(ProfileService.apply _)
}
