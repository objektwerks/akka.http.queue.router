lazy val commonSettings = Defaults.coreDefaultSettings ++ packAutoSettings ++ Seq(
  name := "rest-queue-router",
  version := "0.1-SNAPSHOT",
  organization := "com.here.eva",
  scalaVersion := "2.11.8",
  ivyScala := ivyScala.value map {
    _.copy(overrideScalaVersion = true)
  },
  libraryDependencies ++= {
    val akkaVersion = "2.4.8"
    Seq(
      "com.typesafe.akka" % "akka-actor_2.11" % akkaVersion,
      "com.typesafe.akka" % "akka-stream_2.11" % akkaVersion,
      "com.typesafe.akka" % "akka-http-experimental_2.11" % akkaVersion,
      "com.typesafe.akka" % "akka-slf4j_2.11" % akkaVersion,
      "com.here.eva" % "queue-connector_2.11" % "0.1-SNAPSHOT",
      "ch.qos.logback" % "logback-classic" % "1.1.3"
    )
  },
  scalacOptions ++= Seq(
    "-language:postfixOps",
    "-language:implicitConversions",
    "-feature",
    "-unchecked",
    "-deprecation",
    "-Xlint",
    "-Xfatal-warnings"
  ),
  fork in test := true,
  packCopyDependenciesUseSymbolicLinks := false,
  packResourceDir += (baseDirectory.value / "app.queue.router.conf" -> "bin/app.queue.router.conf"),
  packJvmOpts := Map("master-node" -> Seq("-server", "-Xss1m", "-Xms1g", "-Xmx4g"))
)
lazy val root = (project in file(".")).
  configs(IntegrationTest).
  settings(commonSettings: _*).
  settings(Defaults.itSettings: _*).
  settings(
    libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.6" % "it,test"
  )
