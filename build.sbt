lazy val commonSettings = Seq(
  organization := "com.dwolla",
  homepage := Option(url("https://github.com/Dwolla/akka-memory-usage-logger")),
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  scalaVersion := "2.12.1",
  crossScalaVersions := Seq("2.12.1", "2.11.8"),
  scalacOptions := Seq("-feature", "-deprecation"),
  releaseVersionBump := sbtrelease.Version.Bump.Minor,
  releaseCommitMessage :=
    s"""${releaseCommitMessage.value}
       |
       |[ci skip]""".stripMargin,
  releaseCrossBuild := true
)

lazy val bintraySettings = Seq(
  bintrayVcsUrl := Some("https://github.com/Dwolla/akka-memory-usage-logger"),
  publishMavenStyle := false,
  bintrayRepository := "maven",
  bintrayOrganization := Option("dwolla"),
  pomIncludeRepository := { _ ⇒ false }
)

lazy val akkaMemoryUsageLogger = (project in file("."))
  .settings(
    name := "akka-memory-usage-logger",
    resolvers ++= Seq(
      Resolver.bintrayIvyRepo("dwolla", "maven")
    ),
    libraryDependencies ++= {
      val akkaVersion = "2.4.17"
      val json4sVersion = "3.5.1"
      val specs2Version = "3.8.6"
      Seq(
        "com.typesafe.akka" %% "akka-actor" % akkaVersion % Provided,
        "org.json4s" %% "json4s-native" % json4sVersion % Provided,
        "com.dwolla" %% "testutils" % "1.4.0" % Test,
        "org.specs2" %% "specs2-core" % specs2Version % Test,
        "org.specs2" %% "specs2-mock" % specs2Version % Test,
        "org.specs2" %% "specs2-matcher-extra" % specs2Version % Test
      )
    }
  )
  .settings(commonSettings ++ bintraySettings: _*)
