package json

import cats.effect.{ExitCode, IO, IOApp}
import com.comcast.ip4s._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.ember.server._
import org.http4s.implicits._

object HelloWorldService extends IOApp {

  case class User(name: String)
  case class Hello(greeting: String)

  implicit val decoder = jsonOf[IO, User]

  val jsonApp = HttpRoutes.of[IO] {
    case req @ POST -> Root / "hello" =>
      for {
        // Decode a User request
        user <- req.as[User]
        // Encode a hello response
        resp <- Ok(Hello(user.name).asJson)
      } yield (resp)
  }.orNotFound

  def run(args: List[String]): IO[ExitCode] =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(jsonApp)
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)
}
