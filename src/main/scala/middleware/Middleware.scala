package middleware

import cats.effect.{IO, IOApp}
import cats.data.Kleisli
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._

object Middleware extends IOApp.Simple {

  def myMiddle(service: HttpRoutes[IO], header: Header.ToRaw): HttpRoutes[IO] = Kleisli { (req: Request[IO]) =>
    service(req).map {
      case Status.Successful(resp) =>
        resp.putHeaders(header)
      case resp => resp
    }
  }

  val service = HttpRoutes.of[IO] {
    case GET -> Root / "bad" =>
      BadRequest()
    case _ => Ok()
  }

  val goodRequest: Request[IO] = Request[IO](Method.GET, uri"/")
  val badRequest: Request[IO] = Request[IO](Method.GET, uri"/bad")
  val modifiedService: HttpRoutes[IO] = myMiddle(service, "SomeKey" -> "SomeValue")

  val run: IO[Unit] = modifiedService.orNotFound(goodRequest).map(println).void
}
