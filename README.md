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

## Hands-on Workshop

- Check Java version: 1.8 required
  - java -version 
- Create a free cluster from Yugabyte cloud
  - Install Yugabyte client shell
    curl -sSL https://downloads.yugabyte.com/get_clients.sh | bash
    export YUGABYTE_HOME=/Users/weiwang/yugabyte-client-2.6
  - Connect to the cluster
    SSL_CERTFILE=/Users/weiwang/Downloads/root.crt $YUGABYTE_HOME/bin/ycqlsh 748fdee2-aabe-4d75-a698-a6514e0b19ff.aws.ybdb.io 9042 -u admin --ssl
  - Create keyspace, tables and insert testing data
    yugabyte.sql
- Create truststore to establish ssl connection from Spark to Yugabyte cloud
  cd /Users/weiwang/Documents/spark3yb
  keytool -keystore yb-keystore.jks -storetype 'jks' -importcert -file root.crt -keypass 'ybcloud' -storepass 'ybcloud' -alias ~/Documents/spark3yb/root_crt   noprompt
- Install Spark 3.0 as needed
  - Download Spark 3.0
    wget https://dlcdn.apache.org/spark/spark-3.0.3/spark-3.0.3-bin-hadoop2.7.tgz
    tar xvf spark-3.0.3-bin-hadoop2.7.tgz
    cd spark-3.0.3-bin-hadoop2.7
  - Invoke Spark shell with Yugabyte Spark connector
    $SPARK_HOME/bin/spark-shell --conf spark.cassandra.connection.host=127.0.0.1 \
    --conf spark.sql.extensions=com.datastax.spark.connector.CassandraSparkExtensions \
    --packages com.yugabyte.spark:spark-cassandra-connector_2.12:3.0-yb-8
- Build an application 
 - import libraires

   import org.apache.spark.{SparkConf, SparkContext}
   import org.apache.spark.sql.SparkSession
   import org.apache.spark.sql.Row
   import com.datastax.spark.connector._
   import org.apache.spark.sql.cassandra.CassandraSQLRow
   import org.apache.spark.sql.cassandra._
   import com.datastax.spark.connector.cql.CassandraConnectorConf
   import org.apache.spark.sql.functions._
   import org.apache.spark.sql.expressions.Window
   import com.datastax.spark.connector.cql.CassandraConnector
   
 - YB CLoud connectivity info
   val host = "748fdee2-aabe-4d75-a698-a6514e0b19ff.aws.ybdb.io"
   val keyspace = "test"
   val table = "employees_json"
   val user = "admin"
   val password = "your password for admin"
   val keyStore ="/Users/weiwang/Documents/spark3yb/yb-keystore.jks"
   
 - Create Spark conf
   val conf = new SparkConf().setAppName("yb.spark-jsonb").setMaster("local[1]").set("spark.cassandra.connection.localDC", "us-east   2").set("spark.cassandra.connection.host", "127.0.0.1").set("spark.sql.catalog.ybcatalog",
"com.datastax.spark.connector.datasource.CassandraCatalog").set("spark.sql.extensions", "com.datastax.spark.connector.CassandraSparkExtensions")

- Create Spark session
 val spark = SparkSession.builder().config(conf).config("spark.cassandra.connection.host", host).config("spark.cassandra.connection.port", "9042").config("spark.cassandra.connection.ssl.clientAuth.enabled", true).config("spark.cassandra.auth.username", user).config("spark.cassandra.auth.password", password).config("spark.cassandra.connection.ssl.enabled", true).config("spark.cassandra.connection.ssl.trustStore.type", "jks").config("spark.cassandra.connection.ssl.trustStore.path", keyStore).config("spark.cassandra.connection.ssl.trustStore.password", "ybcloud").withExtensions(new CassandraSparkExtensions).getOrCreate()
 
- Read from YCQL table
  val df_yb = spark.read.table("ybcatalog.test.employees_json")
  
- Perform ETL
  val windowSpec  = Window.partitionBy("department_id").orderBy("salary")
  df_yb.withColumn("row_number",row_number.over(windowSpec)).show(false)
  df_yb.withColumn("rank",rank().over(windowSpec)).show(false)

- Write back to YCQL table
  df_yb.write.cassandraFormat("employees_json_copy", "test").mode("append").save()
  //to verify
  val sqlDF = spark.sql("SELECT * FROM ybcatalog.test.employees_json_copy order by department_id").show(false)
  
- Native Jsonb support demo

 - Using JSONB Column Pruning
val query = "SELECT department_id, employee_id, get_json_object(phone, '$.code') as code FROM ybcatalog.test.employees_json WHERE get_json_string(phone, '$.key(1)') = '1400' order by department_id limit 2";
val df_sel1=spark.sql(query)
df_sel1.explain

- Predicate pushed down	   	   
val query = "SELECT department_id, employee_id, get_json_object(phone, '$.key[1].m[2].b') as key FROM ybcatalog.test.employees_json WHERE get_json_string(phone, '$.key[1].m[2].b') = '1400' order by department_id limit 2";
val df_sel2 = spark.sql(query)
df_sel2.show()
df_sel2.explain

## Session Slides
