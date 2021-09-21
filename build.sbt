name := "spark3yb"

version := "0.1"

scalaVersion := "2.12.10"

scalacOptions := Seq("-unchecked", "-deprecation")

val sparkVersion = "3.0.1"

// maven repo at https://mvnrepository.com/artifact/com.yugabyte.spark/spark-cassandra-connector
libraryDependencies += "com.yugabyte.spark" %% "spark-cassandra-connector-assembly" % "3.0-yb-8"

// maven repo at https://mvnrepository.com/artifact/org.apache.spark/spark-core
libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion

// maven repo at https://mvnrepository.com/artifact/org.apache.spark/spark-sql
libraryDependencies += "org.apache.spark" %% "spark-sql" % sparkVersion
