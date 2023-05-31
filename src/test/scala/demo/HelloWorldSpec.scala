package demo
import demo.HelloWorld.sayHello
import zio._
import zio.test._

import java.io.IOException
object HelloWorldSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] = {
    test("abc") {
      for {
        _ <- sayHello
        output <- TestConsole.output
      } yield assertTrue(output == Vector("Hello, World!\n"))
    }
  }
}

object HelloWorld {
  def sayHello: ZIO[Any, IOException, Unit] =
    Console.printLine("Hello, World!")
}
