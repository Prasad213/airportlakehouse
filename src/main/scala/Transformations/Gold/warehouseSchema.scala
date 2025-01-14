package Transformations.Gold

import DataSource.{DeltaReader, tableReader}
import org.apache.spark.sql.{Dataset, Row, SparkSession}
import org.apache.spark.sql.functions.{col, current_date, lit, to_date}

import scala.xml.NodeSeq

class warehouseSchema {
  def createTables(spark:SparkSession,queries:String, delimiter:String=";"): Unit = {
    queries.split(delimiter).foreach(query => spark.sql(query))
  }

  def getAirportDimdf(spark:SparkSession,params:NodeSeq,extendedPath:String=""):Dataset[Row]={
    val schemaName = (params \ "sourceSchemaName").text
    val tableName = (params \ "sourceTableName").text
    val airportdf = new tableReader().getDataframe(spark,tableName,schemaName)
    airportdf.select(
      col("airport_id").cast("short"),
      col("iata").cast("String"),
      col("icao").cast("String"),
      col("name").cast("String"),
      col("city").cast("String"),
      col("country").cast("String")
    )
  }

  def getFlightScheduleDimdf(spark:SparkSession,params:NodeSeq,extendedPath:String=""):Dataset[Row]={
    val schemaName = (params \ "sourceSchemaName").text
    val tableName = (params \ "sourceTableName").text
    val df = new tableReader().getDataframe(spark,tableName,schemaName)
    df.select(
      col("flightno").cast("String"),
      col("origin").cast("short").alias("origin"),
      col("destination").cast("short").alias("destination"),
      col("departure").cast("String"),
      col("arrival").cast("String"),
      col("airline_id").cast("short"),
      col("weekday").cast("String")
    )

  }

  def getPassengerDimdf(spark:SparkSession,params:NodeSeq,extendedPath:String=""):Dataset[Row]={
    val schemaName = (params \ "sourceSchemaName").text
    val tableName = (params \ "sourceTableName").text
    val df = new tableReader().getDataframe(spark,tableName,schemaName).withColumn("startDate",current_date()).withColumn("endDate",lit("9999-12-31")).withColumn("isCurrent", lit("1"))
    df.select(
      col("passenger_id").cast("int"),
      col("passportno").cast("String"),
      col("firstname").cast("String"),
      col("lastname").cast("String"),
      col("birthdate").cast("DATE"),
      col("sex").cast("String"),
      col("street").cast("String"),
      col("city").cast("String"),
      col("zip").cast("short"),
      col("country").cast("String"),
      col("emailaddress").cast("String"),
      col("telephoneno").cast("String"),
      col("startDate").cast("date"),
      col("endDate").cast("date"),
      col("isCurrent").cast("int")
    )

  }

  def getBookingFactdf(spark:SparkSession,params:NodeSeq,extendedPath:String=""):Dataset[Row]={
    val schemaName = (params \ "sourceSchemaName").text
    val tableNames:Array[String] = (params \ "sourceTableName").text.split(",")
    val bookingdf = new tableReader().getDataframe(spark,tableNames(2),schemaName)
    val flightdf = new tableReader().getDataframe(spark,tableNames(1),schemaName)
    val airlinedf = new tableReader().getDataframe(spark,tableNames(0),schemaName)
    val bookingAndflightdf = bookingdf.join(flightdf,flightdf("flight_id") === bookingdf("flight_id"),"left_outer")
    val bookingFactdf = bookingAndflightdf.join(airlinedf,airlinedf("airline_id") === bookingAndflightdf("airline_id"),"left_outer")
    bookingFactdf.select(
      col("booking_id").cast("int"),
      col("seat").cast("String"),
      col("price").cast("decimal(10,2)"),
      col("flightno").cast("String"),
      col("origin").cast("short"),
      col("destination").cast("short"),
      col("departure").cast("timestamp"),
      col("arrival").cast("timestamp"),
      col("iata").cast("String"),
      col("airlinename").cast("String"),
      col("base_airport").cast("int").alias("airport_id"),
      col("passenger_id").cast("int")
    )

  }

}
