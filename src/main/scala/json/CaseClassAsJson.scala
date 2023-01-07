package json

import cats.effect.{IO, IOApp}
import cats.effect.IOApp.Simple
import io.circe.Encoder
import io.circe.literal.JsonStringContext
import io.circe.syntax._
import io.circe.generic.auto._
import org.http4s.client.dsl.io._
import org.http4s.dsl.io.POST
import org.http4s.implicits.http4sLiteralsSyntax
import org.http4s.circe.CirceEntityEncoder._

object CaseClassAsJson extends IOApp with Simple {

  case class Hello(name: String)
  case class User(name: String)


  /*implicit val HelloEncoder: Encoder[Hello] =
    Encoder.instance { (hello: Hello) =>
      json"""{"hello": ${hello.name}}"""
    }
   */

  POST(User("Bob").asJson, uri"/hello")

  override def run: IO[Unit] = IO(Hello("Renan").asJson).map(println)
}
