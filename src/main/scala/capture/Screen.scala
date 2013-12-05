package capture

import java.awt.Rectangle
import java.awt.image.BufferedImage	
import play.api.libs.iteratee.Enumerator
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** Helper that can grab the screen.  We can use this to generate streams of
 * captured video later.
 */
object Screen {
  // TODO - Do we want to re-use the same robot?
  val robot = new java.awt.Robot()
  def toolkit = java.awt.Toolkit.getDefaultToolkit
  
  
  // TODO - lazy value? This may change...
  def fullScreenSize: Rectangle = {
    new Rectangle(toolkit.getScreenSize)
  }
  def captureScreen(recordArea: Rectangle = fullScreenSize): ScreenCapture = {
    val image = robot.createScreenCapture(recordArea)
    // TODO -  this does not include a mouse cursor, so we may need to add one...
    //val mousePosition = java.awt.MouseInfo.getPointerInfo.getLocation
    ScreenCapture(image)
  }

  
  import play.api.libs.iteratee._
  import concurrent.{ExecutionContext,Future}
  /**
   * A "source" of the screen captures we can drive through a set of iteratee channels.
   */
  def stream(recordArea: Rectangle = fullScreenSize)(implicit ex: ExecutionContext): Enumerator[ScreenCapture] = {
    def retreive(f: Boolean): Future[Option[ScreenCapture]] = 
      Future.successful(Some(captureScreen(recordArea)))
    Enumerator.fromCallback1(retreive, 
        onComplete = () => (), 
        onError = (_,_) => ())
  }
}
