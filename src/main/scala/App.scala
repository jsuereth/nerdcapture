
import javax.swing._
import java.awt.Graphics2D
import java.awt.image.BufferedImageOp

object ScreenApp{
  
  def main(args: Array[String]): Unit = {
    val frame = new javax.swing.JFrame() {
      setTitle("STREAMING VIDEO")
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
      setSize(800, 600)
      setVisible(true)
      
      dummyStream(getGraphics.asInstanceOf[Graphics2D])
    }
  }
  
  import play.api.libs.iteratee._
  def dummyStream(g: Graphics2D) = {
    import concurrent.ExecutionContext.Implicits.global
    val screen = capture.ScreenGrabber.streamSync()
    val smaller = screen &> video.ImageUtils.resize(200, 150)
    val run = smaller apply Iteratee.foreach { snap =>
      g.drawImage(snap.screen, null, 0, 0)
    }
  }
}
