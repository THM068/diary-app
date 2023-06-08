package diaryapp.persistence.model
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

case class Message(message: String, service: String)
object Message {
  implicit val encoder: JsonEncoder[Message] = DeriveJsonEncoder.gen[Message]
  implicit val decoder: JsonDecoder[Message] = DeriveJsonDecoder.gen[Message]
}
