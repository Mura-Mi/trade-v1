enablePlugins(FlywayPlugin)

libraryDependencies += "org.postgresql" % "postgresql" % "42.2.7"

val airframeVersion = "19.9.9.2"

lazy val `test-util` = (project in file("test-util"))
  .settings(
    libraryDependencies ++= Seq(
      "org.wvlet.airframe" %% "airspec" % airframeVersion
    ),
    testFrameworks += new TestFramework("wvlet.airspec.Framework")
  )

lazy val `product-master` = (project in file("product-master"))
  .settings(
    libraryDependencies ++=
      Seq(
        "com.nrinaudo" %% "kantan.csv" % "0.5.1",
        "com.nrinaudo" %% "kantan.csv-generic" % "0.5.1",
        "com.nrinaudo" %% "kantan.csv-java8" % "0.5.1",
        "org.wvlet.airframe" %% "airframe-log" % airframeVersion,
        "com.beachape" %% "enumeratum" % "1.5.13"
      )
    , testFrameworks += new TestFramework("wvlet.airspec.Framework")
  ).dependsOn(persistence, `test-util` % "test")


lazy val `historical-data` = (project in file("historical-data"))
  .settings(
    libraryDependencies ++=
      Seq(
        "com.github.pathikrit" %% "better-files" % "3.8.0",
        "org.wvlet.airframe" %% "airframe-codec" % airframeVersion,
        "org.wvlet.airframe" %% "airframe-log" % airframeVersion,
        "org.jsoup" % "jsoup" % "1.12.1",
        "com.nrinaudo" %% "kantan.csv" % "0.5.1",
        "com.nrinaudo" %% "kantan.csv-generic" % "0.5.1",
        "com.beachape" %% "enumeratum" % "1.5.13"
      )
    , testFrameworks += new TestFramework("wvlet.airspec.Framework")
  ).dependsOn(`product-master`, persistence, `test-util` % "test")

lazy val persistence = (project in file("persistence")).settings(
  libraryDependencies ++=
    Seq(
      "io.getquill" %% "quill-finagle-postgres" % "3.4.9",
    )
  , testFrameworks += new TestFramework("wvlet.airspec.Framework")
).dependsOn(`test-util` % "test")

flywayUrl := "jdbc:postgresql://localhost:5432/dev"
flywayUser := "dev"
flywayPassword := "secret"
flywayLocations += "filesystem:database/migration"
//flywayUrl in Test := "jdbc:hsqldb:file:target/flyway_sample;shutdown=true"
//flywayUser in Test := "SA"
//flywayPassword in Test := ""
