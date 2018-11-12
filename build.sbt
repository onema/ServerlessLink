//resolvers += "Onema Snapshots" at "s3://s3-us-east-1.amazonaws.com/ones-deployment-bucket/snapshots"

lazy val root = (project in file("."))
.settings(
  organization := "io.onema",

  name := "serverlesslink",

  version := "0.1.0",

  scalaVersion := "2.12.7",

  libraryDependencies ++= {
    Seq(
      // Serverless Base!
      "io.onema"                  % "userverless-core_2.12"     % "0.0.2",
      "com.amazonaws"             % "aws-java-sdk-dynamodb"     % "1.11.408",

      // Logging
      "com.typesafe.scala-logging"% "scala-logging_2.12"        % "3.7.2",
      "ch.qos.logback"            % "logback-classic"           % "1.1.7",

      // Testing
      "org.scalatest"             %% "scalatest"                          % "3.0.4"   % Test,
      "org.scalamock"             % "scalamock-scalatest-support_2.12"    % "3.6.0"   % Test
    )
  }
)

// Assembly
assemblyJarName in assembly := "app.jar"

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Xfatal-warnings")


