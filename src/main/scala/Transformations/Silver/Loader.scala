package Transformations.Silver

import DataSink.deltaTableSink
import DataSource.{CSVFileReader, DeltaReader, JDBCReader}
import org.apache.spark.sql.SparkSession

import scala.xml.{Elem, NodeSeq}

object Loader {
  def showTables(spark:SparkSession,readParams:NodeSeq):Unit={
      val tables:NodeSeq =  readParams \ "tables"
      tables.foreach(table => {
        table.text match {
          case "booking" => {
            new DeltaReader().getDataframe(spark,readParams \ "bookingPath").printSchema()
          }
          case _ => {
            new DeltaReader().getDataframe(spark,readParams \ "dbPath",table.text).printSchema()
          }
        }
      })
    }

    def loadToTable(spark: SparkSession,readParams:NodeSeq,schemaName:String,path:String):Unit={
      val tables:NodeSeq =  readParams \ "tables"
      tables.foreach(table => {
        table.text match {
          case "booking" => {
            val df = new CuratedSchema().getBookingdf(spark,readParams \ "bookingPath")
            new deltaTableSink().loadToDeltaTable(df,table.text,schemaName)
          }
          case "airline" => {
            val df = new CuratedSchema().getAirlinedf(spark,readParams \ "dbPath",table.text)
            new deltaTableSink().loadToDeltaTable(df,table.text,schemaName)
          }
          case "airport" =>{
            val df = new CuratedSchema().getAirportdf(spark,readParams \ "dbPath",table.text)
            new deltaTableSink().loadToDeltaTable(df,table.text,schemaName)
          }
          case "flight" => {
            val df = new CuratedSchema().getFlightdf(spark,readParams \ "dbPath",table.text)
            new deltaTableSink().loadToDeltaTable(df,table.text,schemaName)
          }
          case "flightschedule" => {
            val df = new CuratedSchema().getFlightScheduledf(spark,readParams \ "dbPath",table.text)
            new deltaTableSink().loadToDeltaTable(df,table.text,schemaName)
          }
          case "passenger" =>{
            val df = new CuratedSchema().getPassengerdf(spark,readParams \ "dbPath",table.text)
            new deltaTableSink().loadToDeltaTable(df,table.text,schemaName)
          }
        }
      })
    }


}
