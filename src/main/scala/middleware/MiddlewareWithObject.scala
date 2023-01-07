package middleware

import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._

object MiddlewareWithObject extends IOApp.Simple {

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

  val newService = MyMiddle(service, "SomeKey" -> "SomeValue")
  val goodRequest: Request[IO] = Request[IO](Method.GET, uri"/")
  val badRequest: Request[IO] = Request[IO](Method.GET, uri"/bad")

  val run: IO[Unit] = newService.orNotFound(goodRequest).map(println).void

}
