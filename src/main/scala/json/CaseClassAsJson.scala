package json

import cats.effect.IOApp.Simple
import cats.effect.{IO, IOApp}
import io.circe.generic.auto._
import io.circe.syntax._

object CaseClassAsJson extends IOApp with Simple {

  case class Hello(name: String)
  case class User(name: String)


  /*implicit val HelloEncoder: Encoder[Hello] =
    Encoder.instance { (hello: Hello) =>
      json"""{"hello": ${hello.name}}"""
    }
   */

  println(User("Bob").asJson)

  override def run: IO[Unit] = IO(Hello("Renan").asJson).map(println)
}
