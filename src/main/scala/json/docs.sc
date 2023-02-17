import io.circe.generic.auto.{exportDecoder, exportEncoder}
import io.circe.syntax._

val intJson = List(1, 2, 3).asJson

case class User(name: String)

val userJson = User("Renan").asJson

intJson.as[List[Int]]

userJson.as[User]