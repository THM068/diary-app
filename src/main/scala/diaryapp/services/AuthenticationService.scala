package diaryapp.services

import diaryapp.persistence.model.Account
import diaryapp.persistence.model.DbTypes.CollectionRef
import diaryapp.persistence.{AccountRepository, FaunaDbClient, FutureExecutionContext}
import faunadb.query.{Arr, Call}
import faunadb.values.{Codec, Value}
import zio._
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

object LoginCredentials {
  implicit val decoder: JsonDecoder[LoginCredentials] = DeriveJsonDecoder.gen[LoginCredentials]
  implicit val encoder: JsonEncoder[LoginCredentials] = DeriveJsonEncoder.gen[LoginCredentials]

}

case class LoginCredentials(email: String, password: String)
case class Accountesponse(instance: CollectionRef)
case class LoginResponse(name: Option[String], profileId: Option[String])
trait AuthenticationService {
  def login(loginCredentials: LoginCredentials): Task[LoginResponse]
}

case class AuthenticationServiceLive(accountRepository: AccountRepository,faunaDbClient: FaunaDbClient) extends AuthenticationService {
  val LOGIN_TO_ACCOUNT_FUNCTION = "LOGIN_TO_ACCOUNT"
  implicit  val accountCodec: Codec[Accountesponse] = Codec.Record[Accountesponse]
  val faunaClient = faunaDbClient.get

  override def login(loginCredentials: LoginCredentials): Task[LoginResponse] = {
    for {
      accountResponse <- getAccountResponse(loginCredentials)
      account <- getAccount(accountResponse.instance.id)
    }
    yield {
      account match {
        case Some(a) => LoginResponse(name = Some(a.email), profileId = Some(a.profile.id))
        case _ => LoginResponse(None, None)
      }
    }
  }

  private def getAccountResponse(loginCredentials: LoginCredentials): Task[Accountesponse] = {

    ZIO.fromFuture { ec =>
      import FutureExecutionContext._
      for {
        value <- faunaClient.query(Call(
          LOGIN_TO_ACCOUNT_FUNCTION, Arr(Value(loginCredentials.email), Value(loginCredentials.password))
        ))
        partialAccount = value.to[Accountesponse].get
      } yield {
        partialAccount
      }
    }
  }

  private def getAccount(id: String): Task[Option[Account]] = {
    ZIO.fromFuture { ec =>
      accountRepository.find(id)
  }
} }

object AuthenticationService {
  val layer: URLayer[AccountRepository with FaunaDbClient, AuthenticationService] = ZLayer.fromFunction(AuthenticationServiceLive.apply _)
}
