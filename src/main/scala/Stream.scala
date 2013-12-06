import org.gstreamer._

case class Stream(name: String) {
  private val pipeline = new Pipeline(name)
  private var elements: Seq[Element] = Seq.empty
  
  private def hasElement(e: Element): Boolean =
    elements exists (_.getName == e.getName)
  
  private def add(e: Element): Unit = 
    if(!hasElement(e)) {
      pipeline.add(e)
      elements +:= e 
    }
    
  def link(link: (Element, Element)): Stream = {
    val (source, sink) = link
    add(source)
    add(sink)
    source link sink
    this
  }
  
  def addChain(e: Element*): Element = {
    e reduce { (previous, next) =>
      link(previous -> next)
      next
    }
  }
  def addVideo(e: Element): Element = {
    val colors = filters.ffmpeg_colorspace
    val scale = filters.ff_videoscale
    link(e -> colors).link(colors -> scale)
    scale
  }
  
  def start(): Unit = pipeline.play()
  def stop(): Unit = pipeline.stop()
}



