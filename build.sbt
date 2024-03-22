// Common configuration
inThisBuild(
  List(
    version ~= addVersionPadding,
    scalaVersion  := scala3,
    organization  := "io.univalence",
    homepage      := Some(url("https://github.com/univalence/scala3-spark")),
    licenses      := List("Apache-2.0" -> url("https://github.com/univalence/scala3-spark/blob/master/LICENSE")),
    versionScheme := Some("early-semver"),
    developers := List(
      Developer(
        id    = "jwinandy",
        name  = "Jonathan Winandy",
        email = "jonathan@univalence.io",
        url   = url("https://github.com/ahoy-jon")
      ),
      Developer(
        id    = "phong",
        name  = "Philippe Hong",
        email = "philippe@univalence.io",
        url   = url("https://github.com/hwki77")
      ),
      Developer(
        id    = "fsarradin",
        name  = "François Sarradin",
        email = "francois@univalence.io",
        url   = url("https://github.com/fsarradin")
      ),
      Developer(
        id    = "bernit77",
        name  = "Bernarith Men",
        email = "bernarith@univalence.io",
        url   = url("https://github.com/bernit77")
      ),
      Developer(
        id    = "HarrisonCheng",
        name  = "Harrison Cheng",
        email = "harrison@univalence.io",
        url   = url("https://github.com/HarrisonCheng")
      ),
      Developer(
        id    = "dylandoamaral",
        name  = "Dylan Do Amaral",
        email = "dylan@univalence.io",
        url   = url("https://github.com/dylandoamaral")
      )
    )
  )
)

// Scalafix configuration
ThisBuild / scalafixScalaBinaryVersion := "2.4.1"
ThisBuild / semanticdbEnabled          := true
ThisBuild / semanticdbVersion          := scalafixSemanticdb.revision
ThisBuild / scalafixDependencies ++= Seq("com.github.vovapolu" %% "scaluzzi" % "0.1.21")

ThisBuild / javaOptions += "--add-exports=java.base/sun.nio.ch=ALL-UNNAMED"
ThisBuild / javaOptions += "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED"
ThisBuild / Test / javaOptions += "--add-exports=java.base/sun.nio.ch=ALL-UNNAMED"
ThisBuild / Test / javaOptions += "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED"

// SCoverage configuration
val excludedPackages: Seq[String] =
  Seq(
    "zio\\.spark\\.SparkContext.*",                 // Generated source
    "zio\\.spark\\.rdd\\.RDD.*",                    // Generated source
    "zio\\.spark\\.sql\\.Dataset.*",                // Generated source
    "zio\\.spark\\.sql\\.DataFrameNaFunctions.*",   // Generated source
    "zio\\.spark\\.sql\\.DataFrameStatFunctions.*", // Generated source
    "zio\\.spark\\.sql\\.KeyValueGroupedDataset.*", // Generated source
    "zio\\.spark\\.sql\\.implicits.*",              // Spark implicits
    "zio\\.spark\\.sql\\.LowPrioritySQLImplicits.*" // Spark implicits
  )

ThisBuild / coverageFailOnMinimum           := false
ThisBuild / coverageMinimumStmtTotal        := 80
ThisBuild / coverageMinimumBranchTotal      := 80
ThisBuild / coverageMinimumStmtPerPackage   := 50
ThisBuild / coverageMinimumBranchPerPackage := 50
ThisBuild / coverageMinimumStmtPerFile      := 0
ThisBuild / coverageMinimumBranchPerFile    := 0
ThisBuild / coverageExcludedPackages        := excludedPackages.mkString(";")

// Aliases
addCommandAlias("fmt", "scalafmt")
addCommandAlias("fmtCheck", "scalafmtCheckAll")
addCommandAlias("lint", "scalafix")
addCommandAlias("lintCheck", "scalafixAll --check")
addCommandAlias("check", "; fmtCheck; lintCheck;")
addCommandAlias("fixStyle", "; scalafmtAll; scalafixAll;")
addCommandAlias("prepare", "fixStyle")
addCommandAlias("testAll", "; clean;+ test;")
addCommandAlias("testSpecific", "; clean; test;")
addCommandAlias("testSpecificWithCoverage", "; clean; coverage; test; coverageReport;")

// -- Lib versions
lazy val zio        = "2.0.10"
lazy val zioPrelude = "1.0.0-RC19"

lazy val scala3 = "3.4.2-RC1-bin-20240319-4554131-NIGHTLY"


lazy val core =
  (project in file("zio-spark-core"))
    .configs(IntegrationTest)
    .settings(crossScalaVersionSettings)
    .settings(commonSettings)
    .settings(
      name              := "zio-spark",
      libraryDependencies ++= Seq(
        "dev.zio" %% "zio"         % zio,
        "dev.zio" %% "zio-streams" % zio,
        "dev.zio" %% "zio-prelude" % zioPrelude
      ) ++ generateSparkLibraryDependencies
        ++ generateMagnoliaDependency,
      Defaults.itSettings
    )
    .enablePlugins(ZioSparkCodegenPlugin)

lazy val coreTests =
  (project in file("zio-spark-core-tests"))
    .settings(crossScalaVersionSettings)
    .settings(commonSettings)
    .settings(noPublishingSettings)
    .settings(
      javaOptions += "--add-exports=java.base/sun.nio.ch=ALL-UNNAMED",
      name              := "zio-spark-tests",
      libraryDependencies ++= Seq(
        "dev.zio" %% "zio"          % zio,
        "dev.zio" %% "zio-test"     % zio % Test,
        "dev.zio" %% "zio-test-sbt" % zio % Test
      ) ++ generateSparkLibraryDependencies
    )
    .dependsOn(core, test)

lazy val test =
  (project in file("zio-spark-test"))
    .settings(crossScalaVersionSettings)
    .settings(commonSettings)
    .settings(
      name              := "zio-spark-test",
      libraryDependencies ++= Seq(
        "dev.zio" %% "zio"          % zio,
        "dev.zio" %% "zio-test"     % zio,
        "dev.zio" %% "zio-test-sbt" % zio % Test
      ) ++ generateSparkLibraryDependencies
    )
    .dependsOn(core)

def example(project: Project): Project =
  project
    .dependsOn(core)
    .settings(noPublishingSettings)
    .settings(scalacOptions += " -Ytasty-reader")

/*
lazy val exampleSimpleApp              = (project in file("examples/simple-app")).configure(example)
lazy val exampleSparkCodeMigration     = (project in file("examples/spark-code-migration")).configure(example)
lazy val exampleUsingOlderSparkVersion = (project in file("examples/using-older-spark-version")).configure(example)
lazy val exampleWordCount              = (project in file("examples/word-count")).configure(example)
lazy val exampleZIOEcosystem =
  (project in file("examples/zio-ecosystem"))
    .configure(example)
    .dependsOn(
      exampleSimpleApp,
      exampleSparkCodeMigration,
      exampleWordCount
    )

lazy val examples =
  (project in file("examples"))
    .settings(noPublishingSettings)
    .settings(crossScalaVersions := Nil)
    .aggregate(
      exampleSimpleApp,
      exampleSparkCodeMigration,
      exampleUsingOlderSparkVersion,
      exampleWordCount,
      exampleZIOEcosystem
    )

*/


/** Generates required libraries for magnolia. */
lazy val generateMagnoliaDependency: Seq[ModuleID] =
  Seq("com.softwaremill.magnolia1_3" %% "magnolia" % "1.3.4")


/** Generates required libraries for spark. */
lazy val generateSparkLibraryDependencies: Seq[ModuleID] = {
  val sparkVersion: String = "3.5.0"
  val sparkCore            = "org.apache.spark" %% "spark-core" % sparkVersion % Provided withSources ()
  val sparkSql             = "org.apache.spark" %% "spark-sql"  % sparkVersion % Provided withSources ()

  Seq(
    sparkCore.cross(CrossVersion.for3Use2_13),
    sparkSql.cross(CrossVersion.for3Use2_13),
    "io.github.vincenzobaz" %% "spark-scala3-encoders" % "0.2.6",
    "io.github.vincenzobaz" %% "spark-scala3-udf"      % "0.2.6"
  )
}



/**
 * Don't fail the compilation for warnings by default, you can still
 * activate it using system properties (It should always be activated in
 * the CI).
 */
def fatalWarningsAsProperties(options: Seq[String]): Seq[String] =
  if (sys.props.getOrElse("fatal-warnings", "false") == "true") options
  else options.filterNot(Set("-Xfatal-warnings"))

/**
 * Add padding to change: 0.1.0+48-bfcea99ap20220317-1157-SNAPSHOT into
 * 0.1.0+0048-bfcea99ap20220317-1157-SNAPSHOT. It helps to retrieve the
 * latest snapshots from
 * https://oss.sonatype.org/#nexus-search;gav~io.univalence~zio-spark_2.13~~~~kw,versionexpand.
 */
def addVersionPadding(baseVersion: String): String = {
  import scala.util.matching.Regex

  val paddingSize    = 5
  val counter: Regex = "\\+([0-9]+)-".r

  counter.findFirstMatchIn(baseVersion) match {
    case Some(regex) =>
      val count          = regex.group(1)
      val snapshotNumber = "0" * (paddingSize - count.length) + count
      counter.replaceFirstIn(baseVersion, s"+$snapshotNumber-")
    case None => baseVersion
  }
}

def scalaVersionSpecificSources(environment: String, baseDirectory: File)(versions: String*) =
  for {
    version <- "scala" :: versions.toList.map("scala-" + _)
    result = baseDirectory / "src" / environment / version
    if result.exists
  } yield result

def crossScalaVersionSources(scalaVersion: String, environment: String, baseDir: File) = {
  val versions =
    CrossVersion.partialVersion(scalaVersion) match {
      case Some((2, 11)) =>
        List("2", "2.11+", "2.11-2.12")
      case Some((2, 12)) =>
        List("2", "2.11+", "2.12+", "2.11-2.12", "2.12-2.13")
      case Some((2, 13)) =>
        List("2", "2.11+", "2.12+", "2.13+", "2.12-2.13")
      case Some((3, _)) =>
        List("2.11+", "2.12+", "2.13+")
      case _ =>
        List()
    }
  scalaVersionSpecificSources(environment, baseDir)(versions*)
}

lazy val crossScalaVersionSettings =
  Seq(
    Compile / unmanagedSourceDirectories ++= {
      crossScalaVersionSources(
        scalaVersion.value,
        "main",
        baseDirectory.value
      )
    },
    Test / unmanagedSourceDirectories ++= {
      crossScalaVersionSources(
        scalaVersion.value,
        "test",
        baseDirectory.value
      )
    }
  )

lazy val commonSettings =
  Seq(
    resolvers ++= Resolver.sonatypeOssRepos("snapshots"),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    scalacOptions ~= fatalWarningsAsProperties
  )

// run is forcing the exit of sbt. It could be useful to set fork to true
/* however, the base directory of the fork is set to the subproject root (./examples/simple-app) instead of the project
 * root (./) */
/* which lead to errors, eg. Path does not exist:
 * file:./zio-spark/examples/simple-app/examples/simple-app/src/main/resources/data.csv */
lazy val noPublishingSettings =
  Seq(
    fork                                   := false,
    publish / skip                         := true,
    Compile / doc / sources                := Seq.empty,
    Compile / packageDoc / publishArtifact := false
  )
