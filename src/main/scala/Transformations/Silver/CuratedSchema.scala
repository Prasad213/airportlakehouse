package Transformations.Silver

import DataSource.DeltaReader
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.{Dataset, Row, SparkSession}

import scala.xml.NodeSeq

class CuratedSchema {
  def createTables(spark:SparkSession,queries:String, delimiter:String=";"): Unit = {
    queries.split(delimiter).foreach(query => spark.sql(query))
  }

  def getAirlinedf(spark:SparkSession,params:NodeSeq,extendedPath:String=""):Dataset[Row]={
    val df = new DeltaReader().getDataframe(spark,params,extendedPath)
    df.select(
      col("airline_id").cast("short"),
      col("iata").cast("String"),
      col("airlinename").cast("String"),
      col("base_airport").cast("short")
    )
  }

  def getAirportdf(spark:SparkSession,params:NodeSeq,extendedPath:String=""):Dataset[Row]={
    val airportdf = new DeltaReader().getDataframe(spark,params,"airport")
    val airportGeodf = new DeltaReader().getDataframe(spark,params,"airport_geo").select(col("airport_id"),col("city"),col("country"))
    val df = airportdf.join(airportGeodf,airportdf("airport_id") === airportGeodf("airport_id"),"leftouter")
    df.select(
      airportdf("airport_id").cast("short"),
      col("iata").cast("String"),
      col("icao").cast("String"),
      col("name").cast("String"),
      col("city").cast("String"),
      col("country").cast("String")
    )
  }

  def getBookingdf(spark:SparkSession,params:NodeSeq,extendedPath:String=""):Dataset[Row]={
    val df = new DeltaReader().getDataframe(spark,params,extendedPath)
    df.select(
      col("booking_id").cast("int"),
      col("flight_id").cast("int"),
      col("seat").cast("String"),
      col("passenger_id").cast("int"),
      col("price").cast("decimal(10,2)")
    )
  }

  def getFlightdf(spark:SparkSession,params:NodeSeq,extendedPath:String=""):Dataset[Row]={
    val df = new DeltaReader().getDataframe(spark,params,extendedPath)
    df.select(
      col("flight_id").cast("int"),
      col("flightno").cast("String"),
      col("from").cast("short").alias("origin"),
      col("to").cast("short").alias("destination"),
      col("departure").cast("timestamp"),
      col("arrival").cast("timestamp"),
      col("airline_id").cast("short"),
      col("airplane_id").cast("int")
    )
  }

  def getFlightScheduledf(spark:SparkSession,params:NodeSeq,extendedPath:String=""):Dataset[Row]={
    val df = new DeltaReader().getDataframe(spark,params,extendedPath)
      .unpivot(Array(col("flightno"),col("from"),col("to"),col("departure"),col("arrival"),col("airline_id"))
              ,Array(col("monday"),col("tuesday"),col("wednesday"),col("thursday"),col("friday"),col("saturday"),col("sunday"))
                    ,"weekday","isAvailable").where(col("isAvailable") === true)
    df.select(
      col("flightno").cast("String"),
      col("from").cast("short").alias("origin"),
      col("to").cast("short").alias("destination"),
      col("departure").cast("String"),
      col("arrival").cast("String"),
      col("airline_id").cast("short"),
      col("weekday").cast("String")
    )
  }
  def getPassengerdf(spark:SparkSession,params:NodeSeq,extendedPath:String=""):Dataset[Row]={
    val passengerdf = new DeltaReader().getDataframe(spark,params,"passenger")
    val detailsdf = new DeltaReader().getDataframe(spark,params,"passengerdetails")
    val df = passengerdf.join(detailsdf,passengerdf("passenger_id") === detailsdf("passenger_id"),"leftouter")
    df.select(
      passengerdf("passenger_id").cast("int"),
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
      col("telephoneno").cast("String")
    )
  }



}
