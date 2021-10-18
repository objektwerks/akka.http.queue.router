val akkaVersion = "2.6.16"
val akkaHttpVersion = "10.2.6"

lazy val commonSettings = Defaults.coreDefaultSettings ++ Seq(
  name := "akka.http.queue.router",
  organization := "objektwerks",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.13.6",
  libraryDependencies ++= {
    Seq(
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.rabbitmq" % "amqp-client" % "5.13.0",
      "com.iheart" %% "ficus" % "1.5.1",
      "ch.qos.logback" % "logback-classic" % "1.2.6"
    )
  }
)
lazy val root = (project in file(".")).
  configs(IntegrationTest).
  settings(commonSettings: _*).
  settings(Defaults.itSettings: _*).
  settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "it,test",
      "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % "it,test",
      "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "it,test",
      "org.scalatest" %% "scalatest" % "3.2.10" % "it,test"
    )
  )
