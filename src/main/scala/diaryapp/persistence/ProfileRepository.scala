package diaryapp.persistence

import model.{Post, _}
import faunadb.FaunaClient
import faunadb.values.Codec
import zio._


case class ProfileRepository(faunaDbClient: FaunaDbClient) extends FaunaRepository[Profile] {
  override protected val client: FaunaClient = faunaDbClient.get
  override protected val className: String = "Profile"
  override protected val classIndexName: String = "all_profile"
  override implicit protected val codec: Codec[Profile] = Codec.Record[Profile]
}

object ProfileRepository {
  val layer: ZLayer[FaunaDbClient, Nothing, ProfileRepository] = {
    ZLayer {
      for {
        faunaDbClient <- ZIO.service[FaunaDbClient]
      }yield ProfileRepository(faunaDbClient)
    }
  }
}
