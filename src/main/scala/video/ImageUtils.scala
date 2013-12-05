package video


import play.api.libs.iteratee._
import java.awt.image.BufferedImage
import java.awt.Rectangle
import capture.ScreenCapture
import concurrent.ExecutionContext
import java.io.File
import javax.imageio.ImageIO
import org.imgscalr.Scalr
import rx.lang.scala._

object ImageUtils {
  
  def rxCrop(rect: Rectangle)(obs: Observable[ScreenCapture]): Observable[ScreenCapture] = {
    obs map { capture =>
      capture.copy(screen =  capture.screen.getSubimage(rect.x, rect.y, rect.width, rect.height))
    }
  }

  private def resizeCapture(width: Int, height: Int)(capture: ScreenCapture): ScreenCapture = {
    val fixed = 
        Scalr.resize(capture.screen, 
            Scalr.Method.SPEED, 
            Scalr.Mode.FIT_EXACT, 
            width, height,
            Scalr.OP_ANTIALIAS)
      // TODO - Can we flush the original?
      ScreenCapture(fixed, capture.timestamp)
  }
  
  def rxResize(width: Int, height: Int)(obs: Observable[ScreenCapture]): Observable[ScreenCapture] =
    obs map { capture => resizeCapture(width, height)(capture) }
  
  def readImageFile(file: File): BufferedImage =
    ImageIO.read(file)
  
  // TODO - Do we need to repeatedly send this like we do for iteratees?
  def rxStaticImageStream(file: File): Observable[ScreenCapture] = {
    Observable defer {
      Observable(ScreenCapture(ImageIO.read(file)))
    }
  }
    

}