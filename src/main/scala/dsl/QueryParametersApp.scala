package dsl

import cats.effect.{ExitCode, IO, IOApp}
import org.http4s.Method.GET
import org.http4s.dsl.io._
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.{HttpApp, HttpRoutes, QueryParamDecoder}
import org.http4s.server.blaze.BlazeServerBuilder

import java.time.Year

object QueryParametersApp extends IOApp {

  object CountryQueryParamMatcher extends QueryParamDecoderMatcher[String]("country")

  implicit val yearQueryParamDecoder: QueryParamDecoder[Year] =
    QueryParamDecoder[Int].map(Year.of)

  object YearQueryParamMatcher extends QueryParamDecoderMatcher[Year]("year")

  def getAverageTemperatureForCountryAndYear(country: String, year: Year): IO[Double] = IO(42.5)

  val app: HttpApp[IO] = HttpRoutes.of[IO] {
    case GET -> Root => Ok("Hello world")
    case GET -> Root / "weather" / "temperature" :? CountryQueryParamMatcher(country) +& YearQueryParamMatcher(year) =>
      Ok(getAverageTemperatureForCountryAndYear(country, year)
        .map(s"Average temperature for $country in $year was: " + _))
  }.orNotFound

  override def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO](runtime.compute)
      .bindHttp(8080, "localhost")
      .withHttpApp(app)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)

}
