package authentication

import cats._, cats.effect._, cats.implicits._, cats.data._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.server._

object BuiltInApp {

  case class User(id: Long, name: String)

}
