package client

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits.catsSyntaxParallelTraverse1
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.client.JavaNetClientBuilder
import org.http4s.implicits._

object HelloEveryoneApp extends IOApp.Simple {

  def hello(name: String): IO[String] = {
    val target = uri"http://localhost:8080/hello/" / name
    val httpClient: Client[IO] = JavaNetClientBuilder[IO].create
    httpClient.expect[String](target)
  }

  val inputs = List("Ember", "http4s", "Scala")
  val getGreetings: IO[List[String]] = inputs.parTraverse(hello)

  val run: IO[Unit] = EmberClientBuilder
    .default[IO]
    .build
    .use(_ => getGreetings.map(_.foreach(println)))
    .as(ExitCode.Success)

}
