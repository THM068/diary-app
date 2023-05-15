package diaryapp.persistence

import diaryapp.persistence.model.Story
import faunadb.FaunaClient
import faunadb.values.Codec
import zio.{ URLayer, ZLayer}

case class StoryRepository(faunaDbClient: FaunaDbClient) extends FaunaRepository[Story] {
  override protected val client: FaunaClient = faunaDbClient.get
  override protected val className: String = "Story"
  override protected val classIndexName: String = "all_story"
  override implicit protected val codec: Codec[Story] = Codec.Record[Story]
}

object StoryRepository {
  val layer: URLayer[FaunaDbClient, StoryRepository] = ZLayer.fromFunction(StoryRepository.apply _)
}
