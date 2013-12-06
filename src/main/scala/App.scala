
import javax.swing._
import org.gstreamer._

object ScreenApp {
  Gst.init()
  val stream = {
	val stream = new Stream("joined")
    val webcam = {
	  val video = stream.addVideo(sources.webcam())
	  val boxer = filters.video_box(left = -300)
	  stream.link(video, boxer)
	  boxer
	}
	// TODO _ Somethign wrong with screen!
    //val screen = stream.addChain(sources.screen(":0"), filters.videoscale, filters.ffmpeg_colorspace)
	val overlay = stream.addChain((sources.pngVideo(new java.io.File("overlay.png")) :+ filters.ffmpeg_colorspace):_*)
	
    
	// THis mixer code is messed up.
    val mixer = filters.video_mixer
    stream.link(overlay -> mixer)
    //stream.link(screen -> mixer)
    stream.link(webcam -> mixer)
    
    
    val out = sinks.window        
    stream.link(mixer -> out)
    stream
  }
  
  def main(args: Array[String]): Unit = {
     // TODO - Wait for exit
  }
  
 
}
