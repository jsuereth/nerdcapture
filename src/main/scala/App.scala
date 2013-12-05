
import javax.swing._
import java.awt.Graphics2D
import java.awt.image.BufferedImageOp
import java.awt.Rectangle
import java.awt.event.ContainerAdapter
import java.awt.event.ComponentAdapter
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

object ScreenApp {
  
  val system = akka.actor.ActorSystem()
  
  def main(args: Array[String]): Unit = {
    val frame = new javax.swing.JFrame() {
      setTitle("STREAMING VIDEO")
      //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
      setSize(800, 600)
      setVisible(true)
      
      dummyStream(getGraphics.asInstanceOf[Graphics2D])
      addWindowListener(new WindowAdapter {
        override def windowClosed(e: WindowEvent): Unit = {
          system.shutdown()
          Runtime.getRuntime.exit(0)
        }
      })
    }
  }
  
  def dummyStream(g: Graphics2D) = {
    //import concurrent.ExecutionContext.Implicits.global
    //def newSyncContext: concurrent.ExecutionContext = new SyncExecutionContext(200)
    import rx.lang.scala.Scheduler
    //val screen = capture.Screen.rxStream()
    val screen = capture.Screen.rxScreenStreams.last._2
    //val webcam = capture.WebCam.rxStream()
    val overlayImage = video.ImageUtils.readImageFile(new java.io.File("overlay.png"))
    
    val layout = video.Layout(
        size = new Rectangle(800,600),
        items = Seq(
            video.LayoutImage(overlayImage, new Rectangle(0,0, 800,600)),
            video.LayoutStreamItem(screen, new Rectangle(100,200, 200, 300))/*,
            video.LayoutStreamItem(webcam, new Rectangle(300,200, 100, 100))*/
        )
    )
    // We use actors in this stream...
    implicit val actors = system
    val overlayStream = video.Overlays.rxMakeOverlay(layout)
    val run = overlayStream subscribe { snap =>
      g.drawImage(snap.screen, null, 0, 0)
    }
  }
}
