val `ref-data` = (project in file("ref-data"))
  .settings(
    libraryDependencies += "org.wvlet.airframe" %% "airframe-log" % "19.9.9.2"
  )