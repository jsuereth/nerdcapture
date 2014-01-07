package streaming
package filters

import org.gstreamer._
import streaming.ElementWrapper
import streaming.IncreasingCount

class FfmpegColorspace private(override val underlying: Element) extends ElementWrapper
object FfmpegColorspace extends IncreasingCount {
  def apply(): FfmpegColorspace = 
    new FfmpegColorspace(ElementFactory.make("ffmpegcolorspace", "ffmpegcolorspace-${nextCount}"))
}
