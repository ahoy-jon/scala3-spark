package zio.spark.test

import scala3encoders.given // scalafix:ok

import zio.spark.sql.implicits._
import zio.test._

object ZIOSparkSpecDefaultSpec extends ZIOSparkSpecDefault {
  override def sparkSpec =
    suite("ZIOSparkSpecDefault can run spark job without providing layer")(
      test("It can run DataFrame job") {
        for {
          df    <- Dataset(1, 2, 3)
          count <- df.count
        } yield assertTrue(count == 3L)
      }
    )
}