enablePlugins(FlywayPlugin)

libraryDependencies += "org.postgresql" % "postgresql" % "42.2.7"

val airframeVersion = "19.9.9.2"

lazy val `test-util` = (project in file("test-util"))
  .settings(
    libraryDependencies ++= Seq(
      "org.wvlet.airframe" %% "airframe-log" % airframeVersion,
      "org.scalatest" %% "scalatest" % "3.0.8"
    )
  )

lazy val `evaluation` = (project in file("evaluation")).settings(
  libraryDependencies ++= Seq(
    "org.apache.commons" % "commons-math3" % "3.6.1"
  )
).dependsOn(
  `system-base`,
  `product-master`,
  `test-util` % "test"
)

lazy val `product-master` = (project in file("product-master"))
  .settings(
    libraryDependencies ++=
      Seq(
        "com.nrinaudo" %% "kantan.csv" % "0.5.1",
        "com.nrinaudo" %% "kantan.csv-generic" % "0.5.1",
        "com.nrinaudo" %% "kantan.csv-java8" % "0.5.1"
      )
  ).dependsOn(`system-base`, persistence, `test-util` % "test")


lazy val `historical-data` = (project in file("historical-data"))
  .settings(
    libraryDependencies ++=
      Seq(
        "com.github.pathikrit" %% "better-files" % "3.8.0",
        "org.wvlet.airframe" %% "airframe-codec" % airframeVersion,
        "org.jsoup" % "jsoup" % "1.12.1",
        "com.nrinaudo" %% "kantan.csv" % "0.5.1",
        "com.nrinaudo" %% "kantan.csv-generic" % "0.5.1"
      )
  ).dependsOn(`system-base`, `product-master`, persistence, `test-util` % "test")

lazy val `position-analysis` = (project in file("position-analysis"))
  .dependsOn(
    `system-base`,
    `product-master`,
    `historical-data`,
    `test-util` % "test"
  )

lazy val persistence = (project in file("persistence")).settings(
  libraryDependencies ++=
    Seq(
      "io.getquill" %% "quill-finagle-postgres" % "3.4.9",
    )
).dependsOn(`system-base`, `test-util` % "test")

lazy val `system-base` = (project in file("system-base")).settings(
  libraryDependencies ++=
    Seq(
      "org.wvlet.airframe" %% "airframe" % airframeVersion,
      "org.wvlet.airframe" %% "airframe-log" % airframeVersion,
      "com.chuusai" %% "shapeless" % "2.3.3",
      "com.beachape" %% "enumeratum" % "1.5.13"
    )
).dependsOn(`test-util` % "test")

flywayUrl := "jdbc:postgresql://localhost:5432/dev"
flywayUser := "dev"
flywayPassword := "secret"
flywayLocations += "filesystem:database/migration"
//flywayUrl in Test := "jdbc:hsqldb:file:target/flyway_sample;shutdown=true"
//flywayUser in Test := "SA"
//flywayPassword in Test := ""
