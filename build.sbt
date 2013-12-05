name := "nerdstream"

organization := "com.jsuereth"

libraryDependencies ++= 
  Seq(
    "com.typesafe.play" %% "play-iteratees" % "2.2.0",
    "org.imgscalr" % "imgscalr-lib" % "4.2",
    "com.github.sarxos" % "webcam-capture" % "0.3.9"
  )

