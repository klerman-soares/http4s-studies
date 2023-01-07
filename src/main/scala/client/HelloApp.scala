package client

import cats.effect.{ExitCode, IO, IOApp}
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder

object HelloApp extends IOApp.Simple {

  def printHello(client: Client[IO]): IO[Unit] =
    client
      .expect[String]("http://localhost:8080/hello/Adela")
      .flatMap(IO.println)

  val run: IO[Unit] = EmberClientBuilder
    .default[IO]
    .build
    .use(client => printHello(client))
    .as(ExitCode.Success)
}
