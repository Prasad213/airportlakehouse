import org.apache.spark.sql.SparkSession
import java.io.File
import scala.xml.XML.loadFile
import scala.xml.NodeSeq
import scala.xml.Elem


object test extends  App{
  
    val resource:Elem = loadFile("src\\main\\scala\\resources\\resources.xml")

    val bronze = resource \ "bronze"
    (bronze).foreach(cat => {if((cat \ "type").text == "csv" ) {println((cat \ "read_params" \ "path"))}})

    if((bronze \ "type").text == "csv"){
      
      println((bronze \ "read_params").text)
    }

    println((bronze \ "schema").nonEmpty)

    val tables:NodeSeq = (bronze \ "read_params" \ "tables")
    tables.foreach(table => println(table.text))
    
}


    //==============================================================================
    //this is for read write in S3 bucket
    /*val spark = SparkSession.builder().master("local").appName("lakehouse")
      .config("spark.sql.extensions", "io.delta.sql.DeltaSparkSessionExtension")
      .config("spark.sql.catalog.spark_catalog", "org.apache.spark.sql.delta.catalog.DeltaCatalog")
      .config("spark.delta.logStore.class", "org.apache.spark.sql.delta.storage.S3SingleDriverLogStore")
      .config("spark.hadoop.fs.s3a.access.key","c7LzKfIOAi8NeiYr3hKD")
      .config("spark.hadoop.fs.s3a.secret.key","tXt9RUs3MpIzZ5JdDCm4f6CzeWuLiSXDBIHpxaJ9")
      .config("spark.hadoop.fs.s3a.path.style.access", "true")
      .config("spark.hadoop.fs.s3a.endpoint","http://192.168.0.107:9000")
      .config("spark.hadoop.fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
      .getOrCreate()*/
    //============================================================================

    // val spark = SparkSession.builder().master("local").appName("LakehouseApp")
    //   .config("spark.jars.packages","org.apache.hadoop:hadoop-aws:3.3.4,io.delta:delta-spark_2.13:3.2.1,io.unitycatalog:unitycatalog-spark_2.13:0.2.0")
    //   .config("spark.sql.extensions", "io.delta.sql.DeltaSparkSessionExtension")
    //   .config("spark.sql.catalog.spark_catlog", "org.apache.spark.sql.delta.catalog.DeltaCatalog")
    //   .config("spark.hadoop.fs.s3.impl","org.apache.hadoop.fs.s3a.S3AFileSystem")
    //   .config("spark.sql.catalog.lakehouse", "io.unitycatalog.spark.UCSingleCatalog")
    //   .config("spark.sql.catalog.lakehouse.uri", "http://192.168.0.181:8080")
    //   .config("spark.sql.defaultCatalog", "lakehouse")
    //   .getOrCreate()

    // spark.sql("SHOW CATALOGS").show()
    // spark.sql("SHOW SCHEMAS").show()

    /*val csvFile = spark.read.csv("s3a://practice/stations.csv")
    csvFile.show()
    val output_bucket="s3a://practice/station"
    csvFile.write.format("delta").mode("overwrite").save(output_bucket)*/
    //csvFile.write.format("parquet").mode("overwrite").save("s3a://practice/station")
