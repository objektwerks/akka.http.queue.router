lazy val commonSettings = Defaults.coreDefaultSettings ++ Seq(
  name := "akka.http.queue.router",
  organization := "objektwerks",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.12.12",
  libraryDependencies ++= {
    val akkaVersion = "2.5.31"
    val akkaHttpVersion = "10.1.12"
    Seq(
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.rabbitmq" % "amqp-client" % "5.7.3",
      "com.iheart" %% "ficus" % "1.4.7",
      "ch.qos.logback" % "logback-classic" % "1.2.3"
    )
  }
)
lazy val root = (project in file(".")).
  configs(IntegrationTest).
  settings(commonSettings: _*).
  settings(Defaults.itSettings: _*).
  settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http-testkit" % "10.1.12" % "it,test",
      "org.scalatest" %% "scalatest" % "3.0.8" % "it,test"
    )
  )
