package diaryapp.persistence.model
import diaryapp.persistence.model.DbTypes.{CollectionRef, DateCreated}
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

import java.util.Objects

case class Story(id: String,
                 body: String,
                 tags: Seq[String],
                 owner: CollectionRef,
                 dateCreated: DateCreated) extends Entity
object Story {
  def toStoryData(story: Story): StoryData = StoryData(
    id = returnOption(story.id),
    body = story.body,
    tags = returnOption(story.tags),
    profileId = if(Objects.isNull(story.owner)) None else Some(story.owner.id)
  )

  def returnOption[A](e: A): Option[A] = if(Objects.isNull(e)) None else Some(e)

}
case class StoryData(id: Option[String], body: String, tags: Option[Seq[String]], profileId: Option[String] = None)

object StoryData {
  implicit val decoder: JsonDecoder[StoryData] = DeriveJsonDecoder.gen[StoryData]
  implicit val encoder: JsonEncoder[StoryData] = DeriveJsonEncoder.gen[StoryData]
}

case class StoryDataList(stories: Seq[StoryData], before: Option[String], after: Option[String])
object StoryDataList {
  implicit val decoder: JsonDecoder[StoryDataList] = DeriveJsonDecoder.gen[StoryDataList]
  implicit val encoder: JsonEncoder[StoryDataList] = DeriveJsonEncoder.gen[StoryDataList]
}

