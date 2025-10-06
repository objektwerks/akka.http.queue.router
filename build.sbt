val akkaVersion = "2.6.21" // Don't upgrade due to BUSL 1.1!
val akkaHttpVersion = "10.2.10" // Don't upgrade due to BUSL 1.1!

lazy val commonSettings = Defaults.coreDefaultSettings ++ Seq(
  name := "akka.http.queue.router",
  organization := "objektwerks",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.13.17",
  libraryDependencies ++= {
    Seq(
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.rabbitmq" % "amqp-client" % "5.16.0",
      "com.iheart" %% "ficus" % "1.5.2",
      "ch.qos.logback" % "logback-classic" % "1.5.19"
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
      "org.scalatest" %% "scalatest" % "3.2.19" % "it,test"
    )
  )
