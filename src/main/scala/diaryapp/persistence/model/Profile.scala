package diaryapp.persistence.model
import zio.json._

case class Profile(id: String, name: String) extends Entity

case class ReplaceProfileData(name: String)

object Profile {
  implicit val decoder: JsonDecoder[Profile] = DeriveJsonDecoder.gen[Profile]
  implicit val encoder: JsonEncoder[Profile] = DeriveJsonEncoder.gen[Profile]
}

object ReplaceProfileData {
  implicit val decoder: JsonDecoder[ReplaceProfileData] = DeriveJsonDecoder.gen[ReplaceProfileData]
  implicit val encoder: JsonEncoder[ReplaceProfileData] = DeriveJsonEncoder.gen[ReplaceProfileData]
}