package json

import cats.effect.{IO, IOApp}
import org.http4s.client.dsl.io._
import org.http4s.ember.client._
import cats.effect.IOApp.Simple
import io.circe.generic.auto._
import io.circe.syntax.EncoderOps
import org.http4s.circe.{jsonEncoder, jsonOf}
import org.http4s.dsl.io.POST
import org.http4s.implicits.http4sLiteralsSyntax

object HelloWorldClient extends IOApp with Simple {

  case class Hello(greeting: String)
  case class User(name: String)

  def helloClient(name: String): IO[Hello] = {
    // Encode a User request
    val req = POST(User(name).asJson, uri"http://localhost:8080/hello")
    // Create a client
    // Note: this client is used exactly once, and discarded
    // Ideally you should .build.use it once, and share it for multiple requests
    EmberClientBuilder.default[IO].build.use { httpClient =>
      // Decode a Hello response
      httpClient.expect(req)(jsonOf[IO, Hello])
    }
  }

  override def run(): IO[Unit] = helloClient("Alice").map(println)
}
