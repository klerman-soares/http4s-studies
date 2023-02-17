import cats.effect._
import cats.effect.unsafe.IORuntime
import io.circe._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._

class UserAppSpec extends AnyFlatSpec {
  import UserApp._

  implicit val runtime: IORuntime = cats.effect.unsafe.IORuntime.global

  // Return true if match succeeds; otherwise false
  def check[A](actual:        IO[Response[IO]],
               expectedStatus: Status,
               expectedBody:   Option[A])(
                implicit ev: EntityDecoder[IO, A]
              ): Boolean =  {
    val actualResp         = actual.unsafeRunSync()
    val statusCheck        = actualResp.status == expectedStatus
    val bodyCheck          = expectedBody.fold[Boolean](
      // Verify Response's body is empty.
      actualResp.body.compile.toVector.unsafeRunSync().isEmpty)(
      expected => actualResp.as[A].unsafeRunSync() == expected
    )
    statusCheck && bodyCheck
  }

  "Service" should "be able to connect to the websocket server" in {
    // Let's define service by passing a UserRepo that returns Ok(user).
    val repo: UserRepo[IO] = (id: String) => IO.pure(Some(User("johndoe", 42)))

    val expectedJson = Json.obj(
      "name" := "johndoe",
      "age" := 42
    )

    val response: IO[Response[IO]] = httpRoutes[IO](repo).orNotFound.run(
      Request(method = Method.GET, uri = uri"/user/not-used" )
    )

    // expectedJson: Json = JObject(value = object[name -> "johndoe",age -> 42])
    check[Json](response, Status.Ok, Some(expectedJson))
  }

  it should "return none" in {
    // Next, let's define a service with a userRepo that returns None to any input.
    val foundNone: UserRepo[IO] = new UserRepo[IO] {
      def find(id: String): IO[Option[User]] = IO.pure(None)
    }

    val respFoundNone: IO[Response[IO]] = httpRoutes[IO](foundNone).orNotFound.run(
      Request(method = Method.GET, uri = uri"/user/not-used" )
    )
    check[Json](respFoundNone, Status.NotFound, None)
  }

  it should "does not handle" in {
    // Finally, let's pass a Request which our service does not handle.
    val doesNotMatter: UserRepo[IO] = new UserRepo[IO] {
      def find(id: String): IO[Option[User]] =
        IO.raiseError(new RuntimeException("Should not get called!"))
    }

    val respNotFound: IO[Response[IO]] = httpRoutes[IO](doesNotMatter).orNotFound.run(
      Request(method = Method.GET, uri = uri"/not-a-matching-path" )
    )

    check[String](respNotFound, Status.NotFound, Some("Not found"))
  }
}