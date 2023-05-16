ThisBuild / scalaVersion     := "2.12.10"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "husbandryapi",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % "2.0.13",
      "dev.zio" %% "zio-json" % "0.5.0",
      "dev.zio" %% "zio-http" % "3.0.0-RC1",
      "com.faunadb" %% "faunadb-scala" % "4.4.0",
      "dev.zio" %% "zio-test" % "2.0.13" % Test,
      "dev.zio" %% "zio-config" % "4.0.0-RC14",
      "dev.zio" %% "zio-config-typesafe" % "4.0.0-RC14",
      "dev.zio" %% "zio-config-magnolia" % "4.0.0-RC14",
      "io.d11" %% "zhttp" % "2.0.0-RC11",
      "dev.zio" %% "zio-logging-slf4j2" % "2.1.12",
      "dev.zio" %% "zio-test" % "2.0.13" % Test,
      "dev.zio" %% "zio-test-sbt" % "2.0.13" % Test,
      "dev.zio" %% "zio-test-magnolia" % "2.0.13" % Test,
      "com.pauldijou" %% "jwt-json4s-jackson" % "5.0.0"

),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )

