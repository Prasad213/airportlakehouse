package DataSink

import org.apache.spark.sql.{Dataset, Row, SparkSession}

import scala.xml.NodeSeq

class deltaTableSink {
  def loadToDeltaTable(df:Dataset[Row],tableName:String,schemaName:String): Unit= {
    df.write.mode("overwrite").insertInto(schemaName+"."+tableName)
  }
  def mergeToDeltaTable(spark:SparkSession,stagingdf:Dataset[Row], tableName:String, schemaName:String,businessKey:String):Unit={
    stagingdf.createOrReplaceTempView("source")
    spark.sql(s"MERGE INTO ${schemaName}.${tableName} as target " +
      s"USING source ON source.${businessKey} = target.${businessKey} " +
      s"WHEN MATCHED THEN UPDATE SET * " +
      s"WHEN NOT MATCHED THEN INSERT * " +
      s"WHEN NOT MATCHED BY SOURCE THEN DELETE ")
  }


  def mergeToDeltaTableWithSCD2(spark:SparkSession,stagingdf:Dataset[Row], tableName:String, schemaName:String,businessKey:String):Unit={
    stagingdf.createOrReplaceTempView("source")
    spark.sql(s"MERGE INTO ${schemaName}.${tableName} as target " +
      s"USING source ON (source.${businessKey} = target.${businessKey}) and (target.isCurrent = 1) " +
      s"WHEN MATCHED THEN UPDATE SET target.endDate =  current_date(), target.isCurrent = 0 " +
      s"WHEN NOT MATCHED THEN INSERT * " +
      s"WHEN NOT MATCHED BY SOURCE THEN DELETE ")
  }
}

