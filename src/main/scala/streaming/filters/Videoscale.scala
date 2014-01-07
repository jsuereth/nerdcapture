package streaming
package filters

import org.gstreamer._
import streaming.ElementWrapper
import streaming.IncreasingCount

sealed trait VideoScaleMethod
case object GST_VIDEO_SCALE_NEAREST extends VideoScaleMethod
case object GST_VIDEO_SCALE_BILINEAR extends VideoScaleMethod
case object GST_VIDEO_SCALE_4TAP extends VideoScaleMethod
case object GST_VIDEO_SCALE_LANCZOS extends VideoScaleMethod

class VideoScale private(override val underlying: Element) extends ElementWrapper {
  
  def addBorders: Boolean = 
    underlying.get("add-borders").asInstanceOf[Boolean]
  def addBorders_=(value: Boolean): Unit =
    underlying.set("add-borders", value)
    
  def dither: Boolean =
    underlying.get("dither").asInstanceOf[Boolean]
  def dither_=(value: Boolean): Unit =
    underlying.set("dither", value)
    
  def method: VideoScaleMethod =
    underlying.get("method").asInstanceOf[Int] match {
      case 0 => GST_VIDEO_SCALE_NEAREST
      case 1 => GST_VIDEO_SCALE_BILINEAR
      case 2 => GST_VIDEO_SCALE_4TAP
      case 3 => GST_VIDEO_SCALE_LANCZOS
      case n => sys.error(s"${underlying} returns unknown video method enum: ${n}")
    }
  def method_=(value: VideoScaleMethod): Unit = {
    val enumValue = 
      value match {
        case GST_VIDEO_SCALE_NEAREST => 0
        case GST_VIDEO_SCALE_BILINEAR => 1
        case GST_VIDEO_SCALE_4TAP =>  2
        case GST_VIDEO_SCALE_LANCZOS =>  3
      }
    underlying.set("method", enumValue)
  }
  
  def envelope: Double =
    underlying.get("envelope").asInstanceOf[Double]
  def envelope_=(value: Double): Unit =
    underlying.set("envelope", value)
  
  def sharpen: Double =
    underlying.get("sharpen").asInstanceOf[Double]
  def sharpen_=(value: Double): Unit =
    underlying.set("sharpen", value)
    
  def sharpness: Double =
    underlying.get("sharpness").asInstanceOf[Double]
  def sharpness_=(value: Double): Unit =
    underlying.set("sharpness", value)
}
object VideoScale extends IncreasingCount {
  def apply(): VideoScale = 
    new VideoScale(ElementFactory.make("videoscale", s"videoscale-${nextCount}"))
}
