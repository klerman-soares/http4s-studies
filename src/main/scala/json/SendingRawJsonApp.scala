package json

import cats.effect.IOApp.Simple
import cats.effect._
import io.circe._
import io.circe.literal._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._

import org.http4s.circe._ // Json entity encoder comes from here

import org.http4s.client.dsl.io._ // POST comes from here

object SendingRawJsonApp extends IOApp with Simple {


  def hello(name: String): Json =
    json"""{"hello": $name}"""

  val greeting = hello("world")
  // greeting: Json = JObject(value = object[hello -> "world"])

  POST(json"""{"name": "Alice"}""", uri"/hello")

  override def run: IO[Unit] = Ok(greeting).map(println)
}
