package diaryapp.persistence

import diaryapp.persistence.model.{Account, Story}
import faunadb.FaunaClient
import faunadb.values.Codec
import zio.{URLayer, ZLayer}

case class AccountRepository(faunaDbClient: FaunaDbClient) extends FaunaRepository[Account] {
  override protected val client: FaunaClient = faunaDbClient.get
  override protected val className: String = "Account"
  override protected val classIndexName: String = "all_account"
  override implicit protected val codec: Codec[Account] = Codec.Record[Account]
}

object AccountRepository {
  val layer: URLayer[FaunaDbClient, AccountRepository] = ZLayer.fromFunction(AccountRepository.apply _)
}
