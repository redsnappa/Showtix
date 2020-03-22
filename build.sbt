name := "ShowTix"

version := "0.1"

scalaVersion := "2.13.1"

libraryDependencies ++= {
  val akkaVersion = "2.6.3"
  val akkaHttp = "10.1.11"

  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core" % akkaHttp,
    "com.typesafe.akka" %% "akka-http" % akkaHttp,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttp,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttp % "test",
    "org.scalactic" %% "scalactic" % "3.1.1",
    "org.scalatest" %% "scalatest" % "3.1.1" % "test",
    "com.vladsch.flexmark" % "flexmark-all" % "0.35.10" % "test"
  )
}

testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oD")
testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-h", "target/test-reports")

