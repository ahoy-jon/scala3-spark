package zio.spark.sql

import zio.test._

object DataFrameReaderTest extends DefaultRunnableSpec {
  val reader: DataFrameReader = SparkSession.read

  def spec: Spec[Annotations with Live, TestFailure[Any], TestSuccess] =
    dataFrameReaderOptionsSpec + dataFrameReaderOptionDefinitionsSpec

  def dataFrameReaderOptionsSpec: Spec[Annotations with Live, TestFailure[Any], TestSuccess] =
    suite("DataFrameReader Options")(
      test("DataFrameReader should apply options correctly") {
        val options           = Map("a" -> "x", "b" -> "y")
        val readerWithOptions = reader.options(options)

        assertTrue(readerWithOptions.options == options)
      }
    )

  def dataFrameReaderOptionDefinitionsSpec: Spec[Annotations with Live, TestFailure[Any], TestSuccess] = {
    case class ReaderTest(
        testName:      String,
        endo:          DataFrameReader => DataFrameReader,
        expectedKey:   String,
        expectedValue: String
    ) {

      def build: ZSpec[Any, Nothing] =
        test(s"DataFrameReader can add the option (${testName})") {
          val readerWithOptions = endo(reader)
          val options           = Map(expectedKey -> expectedValue)

          assertTrue(readerWithOptions.options == options)
        }
    }

    val tests =
      List(
        ReaderTest(
          testName      = "Any option with a boolean value",
          endo          = _.option("a", value = true),
          expectedKey   = "a",
          expectedValue = "true"
        ),
        ReaderTest(
          testName      = "Any option with a int value",
          endo          = _.option("a", 1),
          expectedKey   = "a",
          expectedValue = "1"
        ),
        ReaderTest(
          testName      = "Any option with a float value",
          endo          = _.option("a", 1f),
          expectedKey   = "a",
          expectedValue = "1.0"
        ),
        ReaderTest(
          testName      = "Any option with a double value",
          endo          = _.option("a", 1d),
          expectedKey   = "a",
          expectedValue = "1.0"
        ),
        ReaderTest(
          testName      = "Option that infer schema",
          endo          = _.inferSchema,
          expectedKey   = "inferSchema",
          expectedValue = "true"
        ),
        ReaderTest(
          testName      = "Option that read header",
          endo          = _.withHeader,
          expectedKey   = "header",
          expectedValue = "true"
        ),
        ReaderTest(
          testName      = "Option that setup delimiter",
          endo          = _.withDelimiter(";"),
          expectedKey   = "delimiter",
          expectedValue = ";"
        )
      )

    suite("DataFrameReader Option Definitions")(tests.map(_.build): _*)
  }
}