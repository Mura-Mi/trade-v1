enablePlugins(FlywayPlugin)

libraryDependencies += "org.postgresql" % "postgresql" % "42.2.7"

val `historical-data` = (project in file("historical-data"))
  .settings(
    libraryDependencies ++=
      Seq(
        "org.wvlet.airframe" %% "airframe-log" % "19.9.9.2",
        "io.getquill" %% "quill-finagle-postgres" % "3.4.9",
        "org.jsoup" % "jsoup" % "1.12.1"
      )
  )

flywayUrl := "jdbc:postgresql://localhost:5432/dev"
flywayUser := "dev"
flywayPassword := "secret"
flywayLocations += "filesystem:database/migration"
//flywayUrl in Test := "jdbc:hsqldb:file:target/flyway_sample;shutdown=true"
//flywayUser in Test := "SA"
//flywayPassword in Test := ""
