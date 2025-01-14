package DataSink

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import scala.xml.NodeSeq

class S3Sink {
  def loadToDeltaFile(df:Dataset[Row],params:NodeSeq,extendedPath:String=""): Unit={
    val method = params \ "method"
    val outputPath = (params \ "path").text
    if(method.nonEmpty){
      df.write.format("delta").mode(method.text).save(outputPath+extendedPath)
    }else{
      df.write.format("delta").save(outputPath+extendedPath)
    }
  }

  //def loadToDeltaFileWithPartition(df:Dataset[Row],method:String,params:String):Unit={???}
}
