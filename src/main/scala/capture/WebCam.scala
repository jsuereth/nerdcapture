package capture

import com.github.sarxos.webcam.{Webcam=>WC}
import java.awt.Rectangle

object WebCam {
  // TODO - Do we want to re-use the same robot?
  private val cam = WC.getDefault
  import play.api.libs.iteratee._
  import concurrent.{ExecutionContext,Future}
  
  
  def cameras: Seq[WC] = {
    import collection.JavaConverters._
    WC.getWebcams.asScala
  }
  
  def cameraStreams(implicit ex: ExecutionContext): Map[String, Enumerator[ScreenCapture]] =
    (cameras map { cam =>
      cam.getName -> stream(cam)
    })(collection.breakOut)
  
  /**
   * A "source" of the screen captures we can drive through a set of iteratee channels.
   */
  def stream(cam: WC = WC.getDefault)(implicit ex: ExecutionContext): Enumerator[ScreenCapture] = {
    def retreive(f: Boolean): Future[Option[ScreenCapture]] = {
      if(!cam.isOpen) cam.open()
      Future.successful(Some(ScreenCapture(cam.getImage)))
    }
    Enumerator.fromCallback1(retreive, 
        onComplete = () => cam.close(), 
        onError = (_,_) => cam.close())
  }
}
