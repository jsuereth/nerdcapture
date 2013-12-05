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
  
  // All the screen streams split by screen id.
  def screenStreams(implicit ex: ExecutionContext): Map[String, Enumerator[ScreenCapture]] =
    (screens map { s => 
      s.getIDstring -> stream(s)
    })(collection.breakOut)
  
  import play.api.libs.iteratee._
  import concurrent.{ExecutionContext,Future}
  /**
   * A "source" of the screen captures we can drive through a set of iteratee channels.
   */
  def stream(screen: GraphicsDevice = defaultScreen, recordArea: Option[Rectangle] = None)(implicit ex: ExecutionContext): Enumerator[ScreenCapture] = {
    val area = recordArea getOrElse screen.getDefaultConfiguration.getBounds
    val robot = new Robot(screen)
    def retreive(f: Boolean): Future[Option[ScreenCapture]] = 
      Future.successful(Some(ScreenCapture(robot.createScreenCapture(area))))
    Enumerator.fromCallback1(retreive, 
        onComplete = () => (), 
        onError = (_,_) => ())
  }
}
