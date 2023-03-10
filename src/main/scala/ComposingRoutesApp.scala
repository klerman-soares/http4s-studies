import cats.effect.{ExitCode, IO, IOApp}
import org.http4s.dsl.io._
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.{HttpApp, HttpRoutes, Request, Response}

// Example of how we can compose roots
object ComposingRoutesApp extends IOApp {
  val route: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "books" / "list" => Ok("List of books")
  }

  val adminRoute: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "categories" / "list" => Ok("Categories") //http://localhost:8080/admin/categories/list
  }

  val app: HttpApp[IO] = Router(
    "/" -> route,
    "/admin" -> adminRoute
  ).orNotFound

  override def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO](runtime.compute)
      .bindHttp(8080, "localhost")
      .withHttpApp(app)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)
}