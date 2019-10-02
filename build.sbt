enablePlugins(FlywayPlugin)

libraryDependencies += "org.postgresql" % "postgresql" % "42.2.7"

val airframeVersion = "19.9.9.2"

val `historical-data` = (project in file("historical-data"))
  .settings(
    libraryDependencies ++=
      Seq(
        "com.github.pathikrit" %% "better-files" % "3.8.0",
        "org.wvlet.airframe" %% "airframe-codec" % airframeVersion,
        "org.wvlet.airframe" %% "airframe-log" % airframeVersion,
        "io.getquill" %% "quill-finagle-postgres" % "3.4.9",
        "org.jsoup" % "jsoup" % "1.12.1",
        "com.nrinaudo" %% "kantan.csv" % "0.5.1",
        "com.nrinaudo" %% "kantan.csv-generic" % "0.5.1"
      )
  )

flywayUrl := "jdbc:postgresql://localhost:5432/dev"
flywayUser := "dev"
flywayPassword := "secret"
flywayLocations += "filesystem:database/migration"
//flywayUrl in Test := "jdbc:hsqldb:file:target/flyway_sample;shutdown=true"
//flywayUser in Test := "SA"
//flywayPassword in Test := ""
