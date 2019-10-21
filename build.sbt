enablePlugins(FlywayPlugin)

lazy val root = (project in file(".")).aggregate(
  domainRoot,
  libRoot,
  persistence,
  `product-master`,
  `position-analysis`,
  `http-service`
)

libraryDependencies += "org.postgresql" % "postgresql" % "42.2.7"

val airframeVersion = "19.10.1"

lazy val `test-util` = (project in file("test-util"))
  .settings(
    libraryDependencies ++= Seq(
      "org.wvlet.airframe" %% "airframe-log" % airframeVersion,
      "org.scalatest" %% "scalatest" % "3.0.8"
    )
  )

lazy val `product-master` = (project in file("product-master"))
  .settings(
    libraryDependencies ++=
      Seq(
        "com.nrinaudo" %% "kantan.csv" % "0.5.1",
        "com.nrinaudo" %% "kantan.csv-generic" % "0.5.1",
        "com.nrinaudo" %% "kantan.csv-java8" % "0.5.1"
      )
  ).dependsOn(`domain-product`, `system-base`, persistence, `test-util` % "test")


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
  ).dependsOn(`system-base`, `domain-market-data`, `product-master`, persistence, `test-util` % "test")

lazy val `position-analysis` = (project in file("position-analysis"))
  .settings(
    libraryDependencies ++=
      Seq("org.wvlet.airframe" %% "airframe" % airframeVersion)
  )
  .dependsOn(
    `system-base`,
    `product-master`,
    `historical-data`,
    `domain-evaluation`,
    `test-util` % "test"
  )

lazy val `http-service` = (project in file("http-service")).settings(
  libraryDependencies ++=
    Seq(
      "org.wvlet.airframe" %% "airframe-http-finagle" % airframeVersion,
      "org.wvlet.airframe" %% "airframe-json" % airframeVersion,
      "org.wvlet.airframe" %% "airframe-codec" % airframeVersion,
      "com.lihaoyi" %% "scalatags" % "0.7.0"
    )
).dependsOn(
  `position-analysis`
)

val domain = "domain"
lazy val domainRoot = (project in new File(domain))
  .aggregate(`domain-product`, `domain-market-data`, `domain-evaluation`)

lazy val domainCommonSettings = Seq(
  libraryDependencies := Seq(
    "org.wvlet.airframe" %% "airframe-log" % airframeVersion,
    "com.chuusai" %% "shapeless" % "2.3.3",
    "com.beachape" %% "enumeratum" % "1.5.13"
  )
)

lazy val `domain-product` = (project in new File(domain, "product"))
  .settings(domainCommonSettings)
  .dependsOn(`lib-enum`, `test-util` % "test")
lazy val `domain-market-data` = (project in new File(domain, "market-data"))
  .settings(domainCommonSettings)
  .dependsOn(`domain-product`, `test-util` % "test")
lazy val `domain-evaluation` = (project in new File(domain, "evaluation"))
  .settings(domainCommonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.apache.commons" % "commons-math3" % "3.6.1"
    )
  )
  .dependsOn(
    `domain-product`,
    `domain-market-data`,
    `test-util` % "test"
  )

lazy val lib = "lib"
lazy val libRoot = (project in file(lib)).aggregate(`lib-enum`)
lazy val `lib-enum` = (project in new File(lib, "enum")).settings(
  libraryDependencies ++= Seq(
    "com.chuusai" %% "shapeless" % "2.3.3",
    "com.beachape" %% "enumeratum" % "1.5.13"
  )
)

lazy val persistence = (project in file("persistence")).settings(
  libraryDependencies ++=
    Seq(
      "io.getquill" %% "quill-finagle-postgres" % "3.4.9",
      "org.wvlet.airframe" %% "airframe" % airframeVersion
    )
).dependsOn(`system-base`, `test-util` % "test", `lib-enum`)

lazy val `system-base` = (project in file("system-base")).settings(
  libraryDependencies ++=
    Seq(
      "org.wvlet.airframe" %% "airframe" % airframeVersion,
      "org.wvlet.airframe" %% "airframe-log" % airframeVersion,
    )
).dependsOn(`test-util` % "test")

flywayUrl := "jdbc:postgresql://localhost:5432/dev"
flywayUser := "dev"
flywayPassword := "secret"
flywayLocations += "filesystem:database/migration"
//flywayUrl in Test := "jdbc:hsqldb:file:target/flyway_sample;shutdown=true"
//flywayUser in Test := "SA"
//flywayPassword in Test := ""
