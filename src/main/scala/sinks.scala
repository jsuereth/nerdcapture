import org.gstreamer._


object sinks {
  
  private lazy val screenSinkFactory = ElementFactory.find("autovideosink")
  def window: Element =
    screenSinkFactory.create("display-sink-"+math.round(math.random*10000L))
}