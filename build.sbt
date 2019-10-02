val `historical-data` = (project in file("historical-data"))
  .settings(
    libraryDependencies ++=
      Seq(
        "org.wvlet.airframe" %% "airframe-log" % "19.9.9.2",
        "io.getquill" %% "quill-finagle-postgres" % "3.4.9",
        "org.jsoup" % "jsoup" % "1.12.1"
      )
  )