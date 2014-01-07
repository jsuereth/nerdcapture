package streaming
package filters

import org.gstreamer._
import streaming.ElementWrapper
import streaming.IncreasingCount

class VideoBox private(override val underlying: Element) extends ElementWrapper {
  
  def left: Int = 
    underlying.get("left").asInstanceOf[Int]
  def left_=(value: Int): Unit =
    underlying.set("left", value)
  def right: Int = 
    underlying.get("right").asInstanceOf[Int]
  def right_=(value: Int): Unit =
    underlying.set("right", value)
  def top: Int = 
    underlying.get("top").asInstanceOf[Int]
  def top_=(value: Int): Unit =
    underlying.set("top", value)
  def bottom: Int = 
    underlying.get("bottom").asInstanceOf[Int]
  def bottom_=(value: Int): Unit =
    underlying.set("bottom", value)
    
  def autocrop: Boolean = 
    underlying.get("autocrop").asInstanceOf[Boolean]
  def autocrop_=(value: Boolean): Unit =
    underlying.set("autocrop", value)
    
  
  def alpha: Double = 
    underlying.get("alpha").asInstanceOf[Double]
  def alpha_=(value: Double): Unit =
    underlying.set("alpha", value)  
    
  def borderAlpha: Double = 
    underlying.get("border-alpha").asInstanceOf[Double]
  def borderAlpha_=(value: Double): Unit =
    underlying.set("border-alpha", value)
    
    
    // TODO - fill
}
object VideoBox extends IncreasingCount {
  def apply(): VideoBox = 
    new VideoBox(ElementFactory.make("videobox", s"videobox-${nextCount}"))
}
