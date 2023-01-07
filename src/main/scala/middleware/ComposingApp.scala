package middleware

import cats.effect._
import cats.implicits.toSemigroupKOps
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._

object ComposingApp extends IOApp.Simple {

  object MyMiddle {
    def addHeader(resp: Response[IO], header: Header.ToRaw) =
      resp match {
        case Status.Successful(resp) => resp.putHeaders(header)
        case resp => resp
      }

    def apply(service: HttpRoutes[IO], header: Header.ToRaw) =
      service.map(addHeader(_, header))
  }

  val service = HttpRoutes.of[IO] {
    case GET -> Root / "bad" =>
      BadRequest()
    case _ => Ok()
  }

  val apiService = HttpRoutes.of[IO] {
    case GET -> Root / "api" =>
      Ok()
  }

  val anotherService = HttpRoutes.of[IO] {
    case GET -> Root / "another" =>
      Ok()
  }

  val aggregateService = apiService <+> MyMiddle(service <+> anotherService, "SomeKey" -> "SomeValue")

  val goodRequest: Request[IO] = Request[IO](Method.GET, uri"/")
  val badRequest: Request[IO] = Request[IO](Method.GET, uri"/bad")
  val apiRequest = Request[IO](Method.GET, uri"/api")

  val run: IO[Unit] = aggregateService.orNotFound(goodRequest).map(println).void

}
