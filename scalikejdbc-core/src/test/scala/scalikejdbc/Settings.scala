package scalikejdbc

import java.util.Properties
import org.scalatest._

trait Settings extends BeforeAndAfter { self: Suite =>

  val props = new Properties
  props.load(classOf[Settings].getClassLoader.getResourceAsStream("jdbc.properties"))
  val driverClassName = props.getProperty("driverClassName")
  val url = props.getProperty("url")
  val user = props.getProperty("user")
  val password = props.getProperty("password")

  def initializeConnectionPools() = {
    if (!ConnectionPool.isInitialized()) {
      Class.forName(driverClassName)
      val poolSettings = new ConnectionPoolSettings(initialSize = 1, maxSize = 100)
      ConnectionPool.singleton(url, user, password, poolSettings)

      try ConnectionPool.get('named)
      catch { case e: IllegalStateException => ConnectionPool.add('named, url, user, password, poolSettings) }
    }
  }

  before {
    initializeConnectionPools()
  }

}
