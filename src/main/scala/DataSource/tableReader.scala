package DataSource

import org.apache.spark.sql.{Dataset, Row, SparkSession}

class tableReader {
  def getDataframe(spark:SparkSession,tableName:String,schema:String):Dataset[Row]={
    spark.read.table(schema+"."+tableName)
  }
}
