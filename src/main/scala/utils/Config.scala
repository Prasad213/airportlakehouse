package utils

import java.util.Properties
import java.io.FileReader

object Config {
  def getConfig(path:String):Properties={
    val file:FileReader = new FileReader(path)
    val prop:Properties = new Properties()
    prop.load(file)
    prop
  }
}
