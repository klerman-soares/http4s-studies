import weaver.SimpleIOSuite
import cats.effect._


// Weaver test is a lean test-framework built on top of cats-effect and fs2

// Suites must be "objects" for them to be picked by the framework
object HelloWeaverTest extends SimpleIOSuite {

  pureTest("non-effectful (pure) test"){
    expect("hello".size == 5)
  }

  private val random = IO(java.util.UUID.randomUUID())

  test("test with side-effects") {
    for {
      x <- random
      y <- random
    } yield expect(x != y)
  }

  loggedTest("test with side-effects and a logger"){ log =>
    for {
      x <- random
      _ <- log.info(s"x : $x")
      y <- random
      _ <- log.info(s"y : $y")
    } yield expect(x != y)
  }
}