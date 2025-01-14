ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.14"

lazy val root = (project in file("."))
  .settings(
    name := "lakehouse"
  )

libraryDependencies ++= Seq(
  "io.unitycatalog" %% "unitycatalog-spark" % "0.2.1",
  "org.apache.spark" %% "spark-sql" % "3.5.3",
  "org.apache.spark" %% "spark-core" % "3.5.3",
  "io.delta" %% "delta-spark" % "3.2.1",
  "org.apache.hadoop" % "hadoop-aws" % "3.4.0",
  "org.apache.hadoop" % "hadoop-common" % "3.4.0",
  "io.minio" % "minio" % "8.5.14"
).map(_.exclude("com.fasterxml.jackson.module", "jackson-module-scala"))



dependencyOverrides ++= Seq( "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.17.0",
                             "com.fasterxml.jackson.core" % "jackson-databind" % "2.17.0")

// https://mvnrepository.com/artifact/com.mysql/mysql-connector-j
libraryDependencies += "com.mysql" % "mysql-connector-j" % "9.1.0"
