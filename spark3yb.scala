package com.yugabyte.sample.apps

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.Row
import com.datastax.spark.connector._
import org.apache.spark.sql.cassandra.CassandraSQLRow
import org.apache.spark.sql.cassandra._
import com.datastax.spark.connector.cql.CassandraConnectorConf
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window

object spark3yb {

 def main(args:Array[String]): Unit = {

val host = "748fdee2-aabe-4d75-a698-a6514e0b19ff.aws.ybdb.io"
val keyspace = "test"
val table = "employees_json"
val user = "admin"
val password = "UMhJyvWzh5wb%JmJrJelHPyY"
val keyStore ="/Users/weiwang/Documents/spark3yb/yb-keystore.jks"

// Setup the local spark master
val conf = new SparkConf()
    .setAppName("yb.spark-jsonb")
    .setMaster("local[1]")
    .set("spark.cassandra.connection.localDC", "us-east-2")
    .set("spark.cassandra.connection.host", "127.0.0.1")
    .set("spark.sql.catalog.ybcatalog", "com.datastax.spark.connector.datasource.CassandraCatalog")
    .set("spark.sql.extensions", "com.datastax.spark.connector.CassandraSparkExtensions")

val spark = SparkSession
      .builder()
      .config(conf)
      .config("spark.cassandra.connection.host", host)
      .config("spark.cassandra.connection.port", "9042")
      .config("spark.cassandra.connection.ssl.clientAuth.enabled", true)
      .config("spark.cassandra.auth.username", user)
      .config("spark.cassandra.auth.password", password)
      .config("spark.cassandra.connection.ssl.enabled", true)
      .config("spark.cassandra.connection.ssl.trustStore.type", "jks")
      .config("spark.cassandra.connection.ssl.trustStore.path", keyStore)
      .config("spark.cassandra.connection.ssl.trustStore.password", "ybcloud")
      .withExtensions(new CassandraSparkExtensions)
      .getOrCreate()

 //example with Spark.sql
runReadWriteSqlExample(spark)

spark.stop()
  }

private def runReadWriteSqlExample(spark: SparkSession): Unit = {

//List namespace
spark.sql("SHOW NAMESPACES FROM ybcatalog").show

//Creating Data Frame by reading testing data from YB cloud database

val df_yb = spark.read.table("ybcatalog.test.employees_json")

df_yb.printSchema()
df_yb.count()
df_yb.show()

//another example: Loading an Dataset using a format helper and a option helper, equivalent to the previous example
val df_yb = spark
  .read
  .cassandraFormat("employees_json", "test")
  .options(ReadConf.SplitSizeInMBParam.option(32))
  .load()

//Performing ETL : Window function
//row_number()
val windowSpec  = Window.partitionBy("department_id").orderBy("salary")
//ranking: row_number() window function is used to give the sequential row number starting from 1 to the result of each //window partition.
 df_yb.withColumn("row_number",row_number.over(windowSpec)).show()

//rank() window function is used to provide a rank to the result within a window partition. This function leaves gaps in rank when there are ties.
df_yb.withColumn("rank",rank().over(windowSpec)).show()

 //writing back  to YCQL: Persisting a Dataset to Database using Save command, following examples are equivalent
 df.write
   .cassandraFormat("employees_json_copy", "test")
   .mode("overwrite")
   .save()
//To verify
val sqlDF = spark.sql("SELECT * FROM ybcatalog.test.employees_json_copy").show(false)


//Native support of Json:
val df = spark.sql("SELECT * FROM ybcatalog.test.employees_json WHERE get_json_object(phone, '$.phone') = 1200");
df.show

//Using JSONB Column Pruning
val query = "SELECT department_id, employee_id, get_json_object(phone, '$.code') as code FROM ybcatalog.test.employees_json WHERE get_json_string(phone, '$.key(1)') = '1400' order by department_id limit 2";
val df_sel1=spark.sql(query)
Df_sel.explain

//Predicate pushed down	   	   
val query = "SELECT department_id, employee_id, get_json_object(phone, '$.key[1].m[2].b') as key FROM ybcatalog.test.employees_json WHERE get_json_string(phone, '$.key[1].m[2].b') = '1400' order by department_id limit 2";

val df_sel2 = spark.sql(query)
df_sel2.show()
df_sel2.explain
println("Json processing successful")
  }
