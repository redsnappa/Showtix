name := "ShowTix"

version := "0.1"

scalaVersion := "2.13.1"

libraryDependencies ++= {
  val akkaVersion = "2.6.3"
  val akkaHttp = "10.1.11"

  Seq(
    "com.typesafe.akka" %% "akka-actor"      % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core"  % akkaHttp,
    "com.typesafe.akka" %% "akka-http"       % akkaHttp,
    "com.typesafe.play" %% "play-ws-standalone-json"       % "2.1.2",
    "com.typesafe.akka" %% "akka-slf4j"      % akkaVersion,
    "ch.qos.logback"    %  "logback-classic" % "1.2.3",
    "de.heikoseeberger" %% "akka-http-play-json"   % "1.31.0",
    "com.typesafe.akka" %% "akka-testkit"    % akkaVersion   % "test",
    "org.scalatest"     %% "scalatest"       % "3.1.1"       % "test"
  )
}
