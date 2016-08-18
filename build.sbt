lazy val commonSettings = Defaults.coreDefaultSettings ++ packAutoSettings ++ Seq(
  name := "rest-queue-router",
  organization := "objektwerks",
  version := "0.1-SNAPSHOT",
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
      "com.typesafe.akka" % "akka-http-spray-json-experimental_2.11" % akkaVersion,
      "com.typesafe.akka" % "akka-slf4j_2.11" % akkaVersion,
      "com.rabbitmq" % "amqp-client" % "3.6.5",
      "net.ceedubs" % "ficus_2.11" % "1.1.2",
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
  packJvmOpts := Map("master-node" -> Seq("-server", "-Xss1m", "-Xms1g", "-Xmx4g"))
)
lazy val root = (project in file(".")).
  configs(IntegrationTest).
  settings(commonSettings: _*).
  settings(Defaults.itSettings: _*).
  settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" % "akka-http-testkit-experimental_2.11" % "2.4.2-RC3" % "it,test",
      "org.scalatest" % "scalatest_2.11" % "2.2.6" % "it,test"
    )
  )
