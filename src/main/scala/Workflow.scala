import org.apache.spark.sql.SparkSession
import utils.Config

import scala.xml.{Elem, NodeSeq}
import Transformations.Bronze.rawFileLoad
import Transformations.Silver.{CuratedSchema, Loader}
import Transformations.Gold.lakehouseLoader
object Workflow {
  def runBronzeWorkflow(spark:SparkSession,resources:Elem):Unit={
     //=====================load raw data files from source to Bronze layer ===================================
  try{
  rawFileLoad.loadRawDataToDelta(spark,resources)
  }catch{
    case e:Exception => println("Error::"+ e.toString)
    }
  }

  def runSilverWorkFlow(spark:SparkSession,resources:Elem,queryFilePath:String):Unit={
    try{
      //Loader.showTables(spark,resources)
      val queriesFile = scala.io.Source.fromFile(queryFilePath)
      val queries = try{queriesFile.mkString} finally {queriesFile.close()}
      new CuratedSchema().createTables(spark,queries)
      val silver:NodeSeq = resources \ "silver"
      val readParams:NodeSeq = silver \ "read_params"
      val schemaName:String = (silver \ "save_params" \ "schemaName").text
      val path:String = (silver \ "save_params" \ "path").text
      Loader.loadToTable(spark,readParams,schemaName,path)
    }catch {
      case e:Exception => println("Error::"+ e.toString)
    }
  }

  def runGoldWorkFlow(spark:SparkSession,resources:Elem,queryFilePath:String):Unit={
    try{
      //Loader.showTables(spark,resources)
      val queriesFile = scala.io.Source.fromFile(queryFilePath)
      val queries = try{queriesFile.mkString} finally {queriesFile.close()}
      new CuratedSchema().createTables(spark,queries)
      val gold:NodeSeq = resources \ "gold"
      val params:NodeSeq = gold \ "params"
      lakehouseLoader.loadToTable(spark,params)
    }catch {
      case e:Exception => println("Error::"+ e.toString)
    }
  }

}
