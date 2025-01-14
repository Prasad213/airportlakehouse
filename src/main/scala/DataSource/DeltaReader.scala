package DataSource

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.SparkSession
import scala.xml.NodeSeq

class DeltaReader {
  def getDataframe(spark:SparkSession,params:NodeSeq,extendedPath:String=""):Dataset[Row]={
    val path = (params \ "path").text
    spark.read.format("delta").load(path+extendedPath)
  }
}
