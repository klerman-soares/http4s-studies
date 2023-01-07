
import RockTheJvmApp.runtime
import cats.data.Kleisli
import cats.effect.{ExitCode, IO, IOApp}
import org.http4s.dsl.io._
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.{HttpApp, HttpRoutes, Request, Response}

object SimpleServer extends IOApp {
  val route: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "length" / str => Ok(str.length.toString)
  }

  val app: HttpApp[IO] = Router(
    "/" -> route
  ).orNotFound

  override def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO](runtime.compute)
      .bindHttp(8080, "localhost")
      .withHttpApp(app)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)
}
