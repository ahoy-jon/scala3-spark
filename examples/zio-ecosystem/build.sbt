name         := "zio-ecosystem"
scalaVersion := "3.4.0"

libraryDependencies ++= Seq(
  // "io.univalence"    %% "zio-spark"  % "X.X.X", //https://index.scala-lang.org/univalence/zio-spark/zio-spark
  "org.apache.spark" %% "spark-core" % "3.5.0" cross(CrossVersion.for3Use2_13),
  "org.apache.spark" %% "spark-sql"  % "3.5.0" cross(CrossVersion.for3Use2_13),
  "dev.zio"          %% "zio-cli"    % "0.5.0"
)
