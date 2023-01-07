package dsl

import cats.effect.{ExitCode, IO, IOApp}
import org.http4s.{HttpApp, HttpRoutes, Response}
import org.http4s.dsl.io._
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.server.blaze.BlazeServerBuilder

import scala.util.Try

import java.time.LocalDate

object DslApp extends IOApp {

  val okIo: IO[Response[IO]] = Ok()
  val okIo2: IO[Response[IO]] = Ok("Hello!)")

  def getUserName(userId: Int): IO[String] = IO(s"The user with user id $userId is Renan")

  object LocalDateVar {
    def unapply(str: String): Option[LocalDate] = {
      if (!str.isEmpty)
        Try(LocalDate.parse(str)).toOption
      else
        None
    }
  }

  def getTemperatureForecast(date: LocalDate): IO[Double] = IO(42.23)

  val app: HttpApp[IO] = HttpRoutes.of[IO] {
    case GET -> Root =>
      okIo2
    case GET -> Root / "hello" / name =>
      Ok(s"Hello, $name")
    case GET -> Root / "users" / IntVar(userId) =>
      Ok(getUserName(userId))
    case GET -> Root / "weather" / "temperature" / LocalDateVar(localDate) =>
      Ok(getTemperatureForecast(localDate)
        .map(s"The temperature on $localDate will be: " + _)) // http://localhost:8080/weather/temperature/2022-12-30
    case GET -> "hello" /: rest =>
      Ok(s"""Hello, ${rest.segments.mkString(" and ")}!""")
    case GET -> IntVar(anInt) /: UUIDVar(anId) /: rest => Ok(s"""Hello $anInt / $anId, ${rest.segments.mkString(" and ")}!""")
    case GET -> Root / file ~ "json" => Ok(s"""{"response": "You asked for $file"}""")
  }.orNotFound

  override def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO](runtime.compute)
      .bindHttp(8080, "localhost")
      .withHttpApp(app)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)
}
