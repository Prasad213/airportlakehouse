CREATE SCHEMA IF NOT EXISTS lakehouse.curatedairportdb;
CREATE TABLE IF NOT EXISTS lakehouse.curatedairportdb.airline (
   airline_id SHORT,
   iata   STRING,
   airlinename   STRING,
   base_airport  SHORT
) using delta location '/silver/curatedairportdb/airline';
CREATE TABLE IF NOT EXISTS lakehouse.curatedairportdb.airport (
   airport_id  SHORT,
   iata   STRING,
   icao   STRING,
   name   STRING,
   city   STRING,
   country   STRING
) using delta location '/silver/curatedairportdb/airport';
CREATE TABLE IF NOT EXISTS lakehouse.curatedairportdb.booking (
   booking_id  INT,
   flight_id  INT,
   seat   STRING,
   passenger_id  INT,
   price  DECIMAL(10,2)
) using delta location '/silver/curatedairportdb/booking';
CREATE TABLE IF NOT EXISTS lakehouse.curatedairportdb.flight (
   flight_id  INT,
   flightno   STRING,
   origin  SHORT,
   destination  SHORT,
   departure  TIMESTAMP,
   arrival  TIMESTAMP,
   airline_id  SHORT,
   airplane_id  INT
) using delta location '/silver/curatedairportdb/flight';
CREATE TABLE IF NOT EXISTS lakehouse.curatedairportdb.flightschedule  (
   flightno   STRING,
   origin  SHORT,
   destination  SHORT,
   departure  STRING,
   arrival  STRING,
   airline_id  SHORT,
   weekday STRING
) using delta location '/silver/curatedairportdb/flightschedule';
CREATE TABLE IF NOT EXISTS lakehouse.curatedairportdb.passenger (
   passenger_id  INT,
   passportno   STRING,
   firstname   STRING,
   lastname   STRING,
   birthdate  DATE,
   sex   STRING,
   street   STRING,
   city   STRING,
   zip  SHORT,
   country   STRING,
   emailaddress   STRING,
   telephoneno   STRING
) using delta location '/silver/curatedairportdb/passenger';