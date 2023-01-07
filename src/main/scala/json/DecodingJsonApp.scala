package json

import cats.effect.{IO, IOApp}
import cats.effect.IOApp.Simple
import org.http4s.circe.jsonOf
import org.http4s.dsl.io.Ok
import org.http4s.dsl.io._

import io.circe.generic.auto.{exportDecoder, exportEncoder}


object DecodingJsonApp extends IOApp with Simple {

  case class User(name: String)

  implicit val userDecoder = jsonOf[IO, User]

  override def run: IO[Unit] = Ok("""{"name":"Renan"}""").flatMap(_.as[User]).map(user => println(user.name))
}
