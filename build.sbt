lazy val commonSettings = Defaults.coreDefaultSettings ++ Seq(
  name := "rest-queue-router",
  organization := "objektwerks",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.12.8",
  libraryDependencies ++= {
    val akkaVersion = "2.5.23"
    val akkaHttpVersion = "10.1.8"
    Seq(
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
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
      "com.typesafe.akka" %% "akka-http-testkit" % "10.1.8" % "it,test",
      "org.scalatest" %% "scalatest" % "3.0.5" % "it,test"
    )
  )
