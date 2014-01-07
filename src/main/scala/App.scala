
import javax.swing._
import org.gstreamer._

object ScreenApp {
  Gst.init()
  val stream = {
	val stream = new GStream("joined")

	val window = {
	  val machine = streaming.x.XImageSrc()
	  machine.displayName = "Battle.net"
	  machine
	}
    val out = sinks.window        
    stream.link(stream.addVideo(window) -> out)
    stream
  }
  
  def main(args: Array[String]): Unit = {
     // TODO - Wait for exit
  }
}
