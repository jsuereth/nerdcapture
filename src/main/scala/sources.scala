import org.gstreamer._
import java.io.File
/** A handful of gstreamer source objects. */
object sources {
  private lazy val webCamFactory = ElementFactory.find("v4l2src")
  
  def possibleWebcams(): Seq[String] = {
    import java.io._
    val f = new File("/dev")
    val results: Array[String] = 
      f.list(new FilenameFilter {
        def accept(f: File, n: String): Boolean = {
          n startsWith "video"
        }
      });
    results.map("/dev/"+)
  }
  private def makeWebcamNameFromLocation(location: String): String = 
    if(location startsWith "/dev/video") s"camera-${location.drop("/dev/video".length)}"
    else location
    
  def webcam(location: String = possibleWebcams.head): Element = {
    val source = webCamFactory.create(makeWebcamNameFromLocation(location))
    source.set("device", location)
    source
  }
  
  
  private lazy val screenSourceFactory = ElementFactory.find("ximagesrc")
  
  // TODO - what displays are available?  There's somethign wrong with this...
  def screen(display: String = ":0"): Element = {
    val element = screenSourceFactory.create("display"+display)
    element.set("display-name", display)
    //element.set("caps", Caps.fromString("video/x-raw-rgb,framerate=5/1"))
    element
  }
  
  
  
  private lazy val fileSourceFactory =   ElementFactory.find("multifilesrc")
  def png(file: File): Element = {
    val element = fileSourceFactory.create(s"overlay:${file.getName}")
    element.set("location", file.getCanonicalPath)
    element.set("caps", Caps.fromString("image/png,framerate=1/1"))
    element
  }
  
  def pngVideo(file: File): Seq[Element] = {
    Seq(
      png(file),
      filters.pngdec, 
      filters.autovideoconvert
    )
  }
  
  def jpeg(file: File): Element = {
    val element = fileSourceFactory.create(s"overlay:${file.getName}")
    element.set("location", file.getCanonicalPath)
    element.set("caps", Caps.fromString("image/jpeg,framerate=1/1"))
    element
  }
}