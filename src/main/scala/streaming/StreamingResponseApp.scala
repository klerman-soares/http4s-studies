package streaming

import scala.concurrent.duration._
import cats.effect._
import com.comcast.ip4s.IpLiteralSyntax
import fs2.Stream
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT

object StreamingResponseApp extends IOApp {

  // An infinite stream of the periodic elapsed time
  val seconds = Stream.awakeEvery[IO](1.second)

  val streamApp = HttpRoutes.of[IO] {
    case GET -> Root / "seconds" =>
      Ok(seconds.map(_.toString)) // `map` `toString` because there's no `EntityEncoder` for `Duration`
  }.orNotFound

  override def run(args: List[String]): IO[ExitCode] = {
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(streamApp)
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)

  }
}
