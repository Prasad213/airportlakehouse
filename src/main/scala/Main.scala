import Transformations.Silver.Loader
import org.apache.spark.sql.SparkSession

import java.util.Properties
import utils.Config
import Workflow.{runBronzeWorkflow, runGoldWorkFlow, runSilverWorkFlow}

import scala.xml.Elem
import scala.xml.XML.loadFile

object Main {
  def main(args: Array[String]): Unit = {
    val prop:Properties = Config.getConfig("src\\main\\scala\\resources\\sparkConfig.properties")

  val spark = SparkSession.builder().master("local").appName("LakehouseApp")
      .config("spark.jars.packages",prop.getProperty("spark.jars.packages"))
      .config("spark.sql.extensions", prop.getProperty("spark.sql.extensions"))
      .config("spark.sql.catalog.spark_catalog", prop.getProperty("spark.sql.catalog.spark_catalog"))
      .config("spark.hadoop.fs.s3.impl",prop.getProperty("spark.hadoop.fs.s3a.impl"))
      .config("spark.sql.catalog.lakehouse", prop.getProperty("spark.sql.catalog.lakehouse"))
      .config("spark.sql.catalog.lakehouse.uri", prop.getProperty("spark.sql.catalog.lakehouse.uri"))
      .config("spark.sql.defaultCatalog",prop.getProperty("spark.sql.defaultCatalog"))
      .config("spark.hadoop.fs.s3a.access.key",prop.getProperty("spark.hadoop.fs.s3a.access.key"))
      .config("spark.hadoop.fs.s3a.secret.key",prop.getProperty("spark.hadoop.fs.s3a.secret.key"))
      .config("spark.hadoop.fs.s3a.path.style.access", prop.getProperty("spark.hadoop.fs.s3a.path.style.access"))
      .config("spark.hadoop.fs.s3a.endpoint",prop.getProperty("spark.hadoop.fs.s3a.endpoint"))
      .config("spark.sql.catalog.spark_catalog", "org.apache.spark.sql.delta.catalog.DeltaCatalog")
      .getOrCreate()

    val resources:Elem = loadFile("src\\main\\scala\\resources\\resources.xml")
    val curatedTableQueriesFilePath = "src\\main\\scala\\resources\\curatedTables.sql"
    val lakehouseTableQueriesFilePath = "src\\main\\scala\\resources\\airportDW.sql"
    //runBronzeWorkflow(spark,resources)
    //runSilverWorkFlow(spark,resources,curatedTableQueriesFilePath)
    //runGoldWorkFlow(spark,resources,lakehouseTableQueriesFilePath)

    spark.sql("select count(*) from lakehouse.airportlakehouse.airportdim").show
    spark.sql("select count(*) from lakehouse.airportlakehouse.bookingfact").show
    spark.sql("select count(*) from lakehouse.airportlakehouse.flightscheduledim").show
    spark.sql("select count(*) from lakehouse.airportlakehouse.passengerdim").show

    //spark.sql("INSERT INTO silver.mytable VALUES (1, \"test 1\")")

  }
}