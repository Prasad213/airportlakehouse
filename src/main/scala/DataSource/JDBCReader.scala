package DataSource

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.SparkSession
import scala.xml.NodeSeq

class JDBCReader {
  def getDataFrame(spark:SparkSession,params:NodeSeq,tableName:String):Dataset[Row]={
    val url = (params \ "jdbcURL").text
    val db = (params \ "database").text
    val userName = (params \ "username").text
    val password = (params \ "password").text
    val driver = (params \ "driver").text

    spark.read.format("jdbc")
      .option("url", url)
      .option("dbtable", db+"."+tableName)
      .option("user", userName)
      .option("password", password)
      .option("driver",driver)
      .load()
  }
}
