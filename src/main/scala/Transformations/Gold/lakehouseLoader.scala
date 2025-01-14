package Transformations.Gold

import DataSink.deltaTableSink
import DataSource.{DeltaReader, tableReader}
import Transformations.Gold.warehouseSchema
import org.apache.spark.sql.SparkSession

import scala.xml.NodeSeq

object lakehouseLoader {

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

  def loadToTable(spark: SparkSession, params:NodeSeq):Unit={
    val tablesData:NodeSeq =  params \ "tables"

    tablesData.foreach(tableParam => {
      val tableName:String = (tableParam \ "tableName").text
      val schemaName:String = (tableParam \ "targetSchemaName").text
      val businessKey:String = (tableParam \ "businessKey").text
      tableName match {
        case "airportdim" => {
          val df = new warehouseSchema().getAirportDimdf(spark,tableParam).except(new tableReader().getDataframe(spark,tableName,schemaName))
          new deltaTableSink().mergeToDeltaTable(spark,df,tableName,schemaName,businessKey)
        }

        case "flightscheduledim" => {
          val df = new warehouseSchema().getFlightScheduleDimdf(spark,tableParam).except(new tableReader().getDataframe(spark,tableName,schemaName))
          new deltaTableSink().mergeToDeltaTable(spark,df,tableName,schemaName,businessKey)
        }

        case "bookingfact" => {
          val df = new warehouseSchema().getBookingFactdf(spark,tableParam).except(new tableReader().getDataframe(spark,tableName,schemaName))
          new deltaTableSink().mergeToDeltaTable(spark,df,tableName,schemaName,businessKey)
        }

        case "passengerdim" =>{
          val df = new warehouseSchema().getPassengerDimdf(spark,tableParam).except(new tableReader().getDataframe(spark,tableName,schemaName))
          new deltaTableSink().mergeToDeltaTableWithSCD2(spark,df,tableName,schemaName,businessKey)
        }
      }
    })
  }

}
