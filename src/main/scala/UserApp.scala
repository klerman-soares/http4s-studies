import cats.effect.{Async, IO, IOApp}
import cats.implicits.toFunctorOps
import io.circe.Encoder
import io.circe.generic.semiauto._
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.implicits.{http4sKleisliResponseSyntaxOptionT, http4sLiteralsSyntax}
import org.http4s._

object UserApp extends IOApp.Simple {

  case class User(name: String, age: Int)
  implicit val UserEncoder: Encoder[User] = deriveEncoder[User]

  trait UserRepo[F[_]] {
    def find(userId: String): F[Option[User]]
  }

  def httpRoutes[F[_]](repo: UserRepo[F])(implicit F: Async[F]): HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "user" / id =>
      repo.find(id).map {
        case Some(user) => Response(status = Status.Ok).withEntity(user.asJson)
        case None       => Response(status = Status.NotFound)
      }
  }

  val repo: UserRepo[IO] = (id: String) => IO.pure(Some(User("johndoe", 42)))
  val request: Request[IO] = Request[IO](Method.GET, uri"/")

  override def run: IO[Unit] = httpRoutes[IO](repo).orNotFound(request).map(println)
}