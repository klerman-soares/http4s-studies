package client

import cats.effect.{IO, IOApp}
import com.comcast.ip4s._
import org.http4s.HttpRoutes
import org.http4s.Method.GET
import org.http4s.client.Client
import org.http4s.dsl.io._
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._

object CallingExternalApiApp extends IOApp.Simple {

  val helloWorldService = (client: Client[IO]) => HttpRoutes.of[IO] {
    case GET -> Root =>
      val url = "https://www.boredapi.com/api/activity"
      val helloEmber: IO[String] = client.expect[String](url)
      Ok(helloEmber)
  }.orNotFound

  val run: IO[Unit] = {
    for {
      client <- EmberClientBuilder.default[IO].build
      _ <-
        EmberServerBuilder.default[IO]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(helloWorldService(client))
          .build
    } yield ()
  }.useForever
}
