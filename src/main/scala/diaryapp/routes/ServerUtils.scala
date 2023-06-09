package diaryapp.routes

import zhttp.http.Request
import zio.{IO, ZIO}
import zio.json.{DecoderOps, JsonDecoder}

object ServerUtils {
  def parseBody[A: JsonDecoder](request: Request): IO[AppError, A] =
    for {
      body   <- request.body.asString.orElseFail(AppError.MissingBodyError)
      parsed <- ZIO.from(body.fromJson[A]).mapError(msg => AppError.JsonDecodingError(msg))
    } yield parsed

}
