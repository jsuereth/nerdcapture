package capture

import java.awt.Rectangle
import java.awt.image.BufferedImage
import play.api.libs.iteratee.Enumerator
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import java.awt.GraphicsEnvironment
import java.awt.GraphicsDevice
import java.awt.Robot

/** Helper that can grab the screen.  We can use this to generate streams of
 * captured video later.
 */
object Screen {
  // TODO - Do we want to re-use the same robot?
  def toolkit = java.awt.Toolkit.getDefaultToolkit
  def screens: Array[GraphicsDevice] = GraphicsEnvironment.getLocalGraphicsEnvironment.getScreenDevices
  def defaultScreen = GraphicsEnvironment.getLocalGraphicsEnvironment.getDefaultScreenDevice 
  
  // ----  RxJava STUFF ------
  import rx.lang.scala._
  def rxStream(screen: GraphicsDevice = defaultScreen, recordArea: Option[Rectangle] = None, synch: Observable[_] = Sync.collectionSynch): Observable[ScreenCapture] = {
    val area = recordArea getOrElse screen.getDefaultConfiguration.getBounds
    val robot = new Robot(screen)
    synch map { _ =>
      ScreenCapture(robot.createScreenCapture(area)) 
    }
  }
  
  def rxScreenStreams: Map[String, Observable[ScreenCapture]] =
    (screens map { s => 
      s.getIDstring -> rxStream(s)
    })(collection.breakOut)
 
}
