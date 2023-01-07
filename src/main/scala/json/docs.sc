import io.circe.generic.auto.{exportDecoder, exportEncoder}
import io.circe.syntax._

val intsJson = List(1, 2, 3).asJson

case class User(name: String)

val userJson = User("Renan").asJson

intsJson.as[List[Int]]

userJson.as[User]