name := "nerdstream"

organization := "com.jsuereth"

libraryDependencies ++= 
  Seq(
    "com.typesafe.play" %% "play-iteratees" % "2.2.0",
    "org.imgscalr" % "imgscalr-lib" % "4.2",
    "com.github.sarxos" % "webcam-capture" % "0.3.9",
    "com.typesafe.akka" %% "akka-actor" % "2.2.0",
    "com.netflix.rxjava" % "rxjava-scala" % "0.15.1",
    "com.googlecode.gstreamer-java" % "gstreamer-java" % "1.5"
  )

