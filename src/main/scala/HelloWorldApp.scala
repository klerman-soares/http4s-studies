import cats.effect._
import com.comcast.ip4s._
import org.http4s.{HttpRoutes, Response, Status}
import org.http4s.dsl.io._

import org.http4s.implicits._
import org.http4s.ember.server._

object HelloWorldApp extends IOApp {

  val helloWorldService = HttpRoutes.of[IO] {
    case GET -> Root / "hello" / name => Ok(s"Hello, $name.")
    case GET -> Root / "request" => Ok("Received a request")
    case GET -> Root / "length" / str => Ok(str.length.toString)
    case _ => IO(Response(Status.Ok))
  }.orNotFound

  def run(args: List[String]): IO[ExitCode] =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(helloWorldService)
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)
}
