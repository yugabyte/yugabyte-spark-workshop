# yugabyte-spark-workshop
Hands-on workshop to build apps using Yugabyte and Spark 3.x

## Prerequisites

- Basic understanding of Apache Spark
- Basic familiarity with YugabyteDB fundamentals - https://docs.yugabyte.com/latest/explore/
- Familiarity with running Linux commands and bash CLI
- Basic experience with Scala and Java
- IDE of choice - Eclipse or IntelliJ or SpringSource Toolkit preferred

## Technical Requirements

- Java JDK 1.8 installed
- Apache Spark 3.0 installed
- Scala 2.12.10 installed
- GitHub account
- SBT 1.5.5 installed
- Cluster access to Yugabyte Cloud: https://www.yugabyte.com/cloud/
- Internet access - ability to access sites via port 80 and 443 (HTTPS)
- Yugabyte Spark Cassandra connector: 3.0-yb-8 https://mvnrepository.com/artifact/com.yugabyte.spark/spark-cassandra-connector

### What will we build in this workshop?
Diagram

- A Spark application with Yugabyte Spark connector to interact with Yugabye cloud to demonstrate how Yugabyte suppports Json data natively
  - Reading from YugabyteDB table
  - Performing ETL operation
  - Writing back to YugabyteDB
  - Column pruning and predicate pushdown

## Agenda

- Overview of YugabyteDB: Distributed SQL Database
- YCQL vs Cassandra
- Apache Spark and YugabyteDB Spark Connector: Key differentiators
- Hands-on Workshop
=======
## Session Slides