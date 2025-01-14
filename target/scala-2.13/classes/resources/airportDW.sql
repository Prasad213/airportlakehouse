CREATE SCHEMA IF NOT EXISTS airportlakehouse;
CREATE TABLE IF NOT EXISTS airportlakehouse.bookingfact (
   booking_id  int,
   seat   String,
   price  decimal(10,2),
   flightno   String,
   origin  short,
   destination  short,
   departure  timestamp,
   arrival  timestamp,
   iata   String,
   airlinename   String,
   airport_id  int,
   passenger_id  int
) using delta location '/gold/airportlakehouse/bookingFact';
CREATE TABLE IF NOT EXISTS airportlakehouse.flightscheduledim (
   flightno   String,
   origin  short,
   destination  short,
   departure  String,
   arrival  String,
   airline_id  short,
   weekday String
) using delta location '/gold/airportlakehouse/flightScheduleDim';
CREATE TABLE IF NOT EXISTS airportlakehouse.passengerdim (
   passenger_id  int,
   passportno   String,
   firstname   String,
   lastname   String,
   birthdate  date,
   sex   String,
   street   String,
   city   String,
   zip  short,
   country   String,
   emailaddress   String,
   telephoneno   String,
   startDate date,
   endDate date,
   isCurrent int
) using delta location '/gold/airportlakehouse/passengerDim';
CREATE TABLE IF NOT EXISTS airportlakehouse.airportdim (
   airport_id  short,
   iata   String,
   icao   String,
   name   String,
   city   String,
   country   String
) using delta location '/gold/airportlakehouse/airportDim';