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
  
  // -- RxJava STUFF --
  import rx.lang.scala._
  def rxStream(cam: WC = WC.getDefault, sync: Observable[_] = Sync.collectionSynch): Observable[ScreenCapture] = {
    sync map { _ =>
      if(!cam.isOpen) cam.open()
      ScreenCapture(cam.getImage)
    } finallyDo { () => cam.close() }
  }
  def rxCameraStreams(sync: Observable[_] = Sync.collectionSynch): Map[String, Observable[ScreenCapture]] = {
    (cameras.map { cam =>
      cam.getName -> rxStream(cam, sync)
    })(collection.breakOut)
  }
  
}
