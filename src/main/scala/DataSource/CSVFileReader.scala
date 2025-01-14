package DataSource

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.SparkSession
import scala.xml.NodeSeq

class CSVFileReader {
    def getDataFrame(spark:SparkSession,params:NodeSeq,extendedPath:String=""):Dataset[Row]={
        val path = (params \ "path").text
        if((params \ "schema").nonEmpty ){
            val schema = (params \ "schema").text
            spark.read.format("csv").option("header","true").schema(schema).load(extendedPath+path)

        }else{
            spark.read.format("csv").option("header","true").load(extendedPath+path)
        }

        //spark.read.format("csv").load("")
    }
}
