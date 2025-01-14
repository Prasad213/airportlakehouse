package Transformations.Bronze

import scala.xml.Elem
import DataSource.CSVFileReader
import org.apache.spark.sql.SparkSession
import DataSink.S3Sink
import DataSource.JDBCReader
import scala.xml.NodeSeq

object rawFileLoad {
  def loadRawDataToDelta(spark:SparkSession,resources:Elem):Unit={
    
        val bronze:NodeSeq = resources \ "bronze"
        bronze.foreach{node =>
        (node \ "type").text match {
          case "mysql" => {
            val readParams = (node \ "read_params")
            val loadParams = (node \ "save_params")
            val tables = (readParams \ "tables")
            tables.foreach(table => {
              val df = new JDBCReader().getDataFrame(spark,readParams,table.text)
              new S3Sink().loadToDeltaFile(df,loadParams,table.text)
            })
          }
          case "csv" => {
            val readParams = (node \ "read_params")
            val loadParams = (node \ "save_params")
            val rawCSVDF = new CSVFileReader().getDataFrame(spark,readParams)
            new S3Sink().loadToDeltaFile(rawCSVDF,loadParams)
          }
          case other:String => println(s"Unknown data source type in bronze for ${other}")
    }}

        
  }
}
