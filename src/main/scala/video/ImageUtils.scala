package video


import play.api.libs.iteratee._
import java.awt.image.BufferedImage
import java.awt.Rectangle
import capture.ScreenCapture
import concurrent.ExecutionContext
import java.io.File
import javax.imageio.ImageIO
import org.imgscalr.Scalr

object ImageUtils {

  /** Converts the Enumerator so that it grabs a smaller rectangle... */
  def crop(rect: Rectangle)(implicit ex: ExecutionContext): Enumeratee[ScreenCapture, ScreenCapture] =
    Enumeratee.map[ScreenCapture] { capture =>
      capture.copy(screen =  capture.screen.getSubimage(rect.x, rect.y, rect.width, rect.height))
    }
  
  def resize(width: Int, height: Int)(implicit ex: ExecutionContext): Enumeratee[ScreenCapture, ScreenCapture] =
    Enumeratee.map[ScreenCapture] { capture =>
      val fixed = 
        Scalr.resize(capture.screen, 
            Scalr.Method.SPEED, 
            Scalr.Mode.FIT_EXACT, 
            width, height,
            Scalr.OP_ANTIALIAS)
      // TODO - Can we flush the original?
      ScreenCapture(fixed, capture.timestamp)
    }
  
  def readImageFile(file: File): BufferedImage =
    ImageIO.read(file)
  
 def staticImage(file: File)(implicit ex: ExecutionContext): Enumerator[ScreenCapture] =
    Enumerator.repeat(ScreenCapture(ImageIO.read(file)))
}

case class LayoutItem(source: Enumerator[ScreenCapture], location: Rectangle)