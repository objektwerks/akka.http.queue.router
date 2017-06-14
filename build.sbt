lazy val commonSettings = Defaults.coreDefaultSettings ++ Seq(
  name := "rest-queue-router",
  organization := "objektwerks",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.12.2",
  ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) },
  libraryDependencies ++= {
    val akkaVersion = "2.5.2"
    val akkaHttpVersion = "10.0.7"
    Seq(
      "com.typesafe.akka" % "akka-actor_2.12" % akkaVersion,
      "com.typesafe.akka" % "akka-stream_2.12" % akkaVersion,
      "com.typesafe.akka" % "akka-slf4j_2.12" % akkaVersion,
      "com.typesafe.akka" % "akka-http_2.12" % akkaHttpVersion,
      "com.typesafe.akka" % "akka-http-spray-json_2.12" % akkaHttpVersion,
      "com.rabbitmq" % "amqp-client" % "4.1.1",
      "com.iheart" % "ficus_2.12" % "1.4.1",
      "ch.qos.logback" % "logback-classic" % "1.2.3"
    )
  },
  scalacOptions ++= Seq(
    "-language:postfixOps",
    "-language:reflectiveCalls",
    "-language:implicitConversions",
    "-language:higherKinds",
    "-feature",
    "-Ywarn-unused-import",
    "-Ywarn-unused",
    "-Ywarn-dead-code",
    "-unchecked",
    "-deprecation",
    "-Xfatal-warnings",
    "-Xlint:missing-interpolator",
    "-Xlint"
  ),
  fork in test := true
)
lazy val root = (project in file(".")).
  configs(IntegrationTest).
  settings(commonSettings: _*).
  settings(Defaults.itSettings: _*).
  settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" % "akka-http-testkit_2.12" % "10.0.7" % "it,test",
      "org.scalatest" % "scalatest_2.12" % "3.0.3" % "it,test"
    )
  )