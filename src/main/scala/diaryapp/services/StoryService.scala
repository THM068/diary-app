package diaryapp.services
import diaryapp.persistence.StoryRepository
import diaryapp.persistence.model.{Page, PaginationOptions, Story}
import zio.{Task, URLayer, ZIO, ZLayer}

import java.util.Objects
final case class StoryService(private val storyRepository: StoryRepository) {

  def save(story: Story): Task[Story] = {
    if(Objects.isNull(story.body) || story.body.isEmpty) {
      return ZIO.fail(new StoryBodyMissing("Missing body"))
    }

    ZIO.fromFuture { implicit ec =>
      storyRepository.save(story)
    }
  }
  def find(id: String): Task[Option[Story]] = {
    ZIO.fromFuture { implicit ec =>
      storyRepository.find(id)
    }
  }

  def nextId(): Task[String] = {
    ZIO.fromFuture { implicit ec =>
      storyRepository.nextId()
    }
  }

  def findAll(): Task[Page[Story]] = {
    implicit val page: PaginationOptions = PaginationOptions(size = Some(100), None, None)
    ZIO.fromFuture { implicit ec =>
      storyRepository.findAll()
    }
  }
}

object StoryService {
  val layer: URLayer[StoryRepository, StoryService] = ZLayer.fromFunction(StoryService.apply _)
}

sealed trait StoryErrors

case class StoryBodyMissing(message: String) extends Throwable with StoryErrors
case class ProfileIdMissing(message: String) extends Throwable with StoryErrors
