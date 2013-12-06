import org.gstreamer._


object filters {
  @volatile private var id = 0
  
  def nextCount: Int = synchronized {
    id += 1
    id
  }
  
  def ffmpeg_colorspace = {
    ElementFactory.make("ffmpegcolorspace", "colorspace" + nextCount)
  }
  def ff_videoscale = ElementFactory.make("ffvideoscale", "videoscale" + nextCount)
  def videoscale = ElementFactory.make("videoscale", "videoscale" + nextCount)
  
  
  def video_box(left: Int = 0, right: Int = 0, top: Int = 0, bottom: Int = 0, autocrop: Boolean = false, alpha: Double= 1.0f, borderAlpha: Double = 1.0) = {
    val boxer = ElementFactory.make("videobox", "boxer"+nextCount)
    boxer.set("left", left)
    boxer.set("right", right)
    boxer.set("top", top)
    boxer.set("bottom", bottom)
    boxer.set("alpha", alpha)
    boxer.set("border-alpha", borderAlpha)
    boxer.set("autocrop", autocrop)
    boxer
  }
  
  
  def video_mixer = {
    val mixer = ElementFactory.make("videomixer", "mixer"+nextCount)
    mixer
  }
  
  
  def pngdec = ElementFactory.make("pngdec", s"pngdec${nextCount}")
  def autovideoconvert = ElementFactory.make("autovideoconvert", s"videoconvert${nextCount}")
}