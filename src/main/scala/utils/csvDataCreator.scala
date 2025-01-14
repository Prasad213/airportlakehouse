package utils

import io.minio.{MinioClient, ObjectWriteResponse, UploadObjectArgs}

import java.io.{File, FileWriter, IOException, PrintWriter}
import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}
import java.util.Properties
import scala.util.{Failure, Success, Try}

 
object csvDataCreator {
 def main(args:Array[String]):Unit={
  println("Main method started...")
  val prop:Properties = Config.getConfig("src\\main\\scala\\resources\\csvDataCreatorConfig.properties")
    
  //===========================Database Connection===================================

   val url:String = prop.getProperty("mysql_url") +"/"+ prop.getProperty("mysql_db1")
   val username:String = prop.getProperty("mysql_user")
   val password:String = prop.getProperty("mysql_password")

   var connection:Connection = null
   var tempfilename:String = null

   try{
      Class.forName("com.mysql.cj.jdbc.Driver")
      connection = DriverManager.getConnection(url,username,password)
      var offset = 0; var batchSize = 5000000;
      val query = "SELECT * FROM booking LIMIT ? OFFSET ?"     
      val statement:PreparedStatement = connection.prepareStatement(query,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE)
      statement.setInt(1,batchSize)
      statement.setInt(2,offset)
      var resultSet:ResultSet = statement.executeQuery()
      val metadata=resultSet.getMetaData()
      val columNames = (1 to metadata.getColumnCount()).map(metadata.getColumnName(_)).mkString(",")
      //=====================append to file=============================
      //val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMddyyyyHHmmss"))
      tempfilename = "temp.csv"
      val filewriter = new FileWriter(tempfilename,true)
      val writer = new PrintWriter(filewriter)
      writer .println(columNames)
      println("Query Executed....")
  
      while(resultSet.next()){
        resultSet.beforeFirst()
        val data = LazyList.continually((resultSet.next(),resultSet)).takeWhile(_._1)
                                          .map{case(hasNext,resultSet)=>{(resultSet.getInt(1),
                                                                          resultSet.getInt(2),
                                                                          resultSet.getString(3),
                                                                          resultSet.getInt(4),
                                                                          resultSet.getDouble(5)).toString().tail.init}}
        
        println("data size is "+data.size)
        // val csvData = data.mkString("\n")
        try{
          data.foreach(writer.println(_))
        }catch{
          case e:IOException => e.printStackTrace()
        }
        offset+=batchSize
        statement.setInt(1,batchSize)
        statement.setInt(2,offset)
        println("query executing.....  "+offset)
        resultSet = statement.executeQuery()
      }
      writer.close()
      filewriter.close()
    
      //=====================minio client creation=========================================
      val minioClient:MinioClient = MinioClient.builder()
       .endpoint(prop.getProperty("source_minio_uri"))  
       .credentials(prop.getProperty("source_minio_accessKey"),prop.getProperty("source_minio_secretKay"))
       .build()
     println("upload file started.....")
      //==============================upload File to Object=============================================
      val bucketName = prop.getProperty("source_minio_bucket")
      val filename = prop.getProperty("booking_csv_path")
      val writeDone:Try[ObjectWriteResponse] = Try(minioClient.uploadObject(
        UploadObjectArgs.builder().bucket(bucketName).`object`(filename).filename(tempfilename).build()
        )) 
      writeDone match {
        case Success(response) => println(response.`object`() + " is written in " + response.bucket()) 
        case Failure(exception) => println("Error:" + exception.getMessage())
      } 
      
    }catch{
        case e:Exception => e.printStackTrace()
    }finally{
      if (connection != null && !connection.isClosed) { 
        connection.close()
      }
      try {new File(tempfilename).delete()}
        catch{case e:IOException => e.printStackTrace()}
      
    }
 }
}