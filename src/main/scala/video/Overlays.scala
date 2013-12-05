package video

import play.api.libs.iteratee._
import java.awt.image.BufferedImage
import java.awt.{Rectangle, Color}
import capture.ScreenCapture
import concurrent.ExecutionContext
import java.io.File
import javax.imageio.ImageIO
import org.imgscalr.Scalr

case class Layout(size: Rectangle, items: Seq[LayoutItem])
case class LayoutItem(source: Enumerator[ScreenCapture], location: Rectangle)

case class LayoutFrameRequestItem(id: Int, capture: ScreenCapture, location: Rectangle)
case class LayoutFrameRequest(size: Rectangle, items: Seq[LayoutFrameRequestItem])

object Overlays {
  val clear = new Color(0.0f, 0.0f, 0.0f, 0.0f)
  def layoutFrame(req: LayoutFrameRequest): ScreenCapture = {
    // TODO - Do we need transparency here? probably...
    val img = new BufferedImage(req.size.width, req.size.height, BufferedImage.TYPE_INT_ARGB)
    val g = img.getGraphics
    for(item <- req.items) {
      // TODO - resizing should already done before we get here...
      g.drawImage(item.capture.screen, item.location.x, item.location.y, item.location.width, item.location.height, clear, null)
    }
    ScreenCapture(img)
  }
  
  
  def makeOverlay(layout: Layout)(implicit ex: ExecutionContext): Enumerator[ScreenCapture] = {
    val fixedEnums: Seq[Enumerator[LayoutFrameRequestItem]] = 
      for {
        (LayoutItem(source, location), idx) <- layout.items.zipWithIndex
        resized = source &> ImageUtils.resize(location.width, location.height)
      } yield resized.map { screen => LayoutFrameRequestItem(idx, screen, location) }
    
    val allFrameRequests: Enumerator[LayoutFrameRequestItem] = Concurrent.patchPanel[LayoutFrameRequestItem] { panel =>
      fixedEnums foreach panel.patchIn
    }
    val constructFrame: Iteratee[LayoutFrameRequestItem, LayoutFrameRequest] =  {
      def step(frame: LayoutFrameRequest): K[LayoutFrameRequestItem, LayoutFrameRequest] = {
        case Input.Empty => Cont(step(frame))
        case Input.EOF => 
          if(frame.items.length == layout.items.length) Done(frame, Input.EOF)
          else Error("Expected remaining frame portions", Input.EOF)
        case Input.El(e) => 
          println("Got another frame from source: " + e.id)
          // Overwrite old overlay value with new image in case we're behind in rendering.
          val items = frame.items.filterNot(_.id == e.id) :+ e
          val nextFrame = frame.copy(items = items)
          if(items.length >= layout.items.length) {
            println("Completed frame!")
            Done(nextFrame, Input.Empty)
          } else Cont(step(nextFrame))
      }
      Cont(step(LayoutFrameRequest(layout.size, Seq.empty)))
    }
    allFrameRequests &> Enumeratee.grouped(constructFrame) &> Enumeratee.map(layoutFrame)
  }  
}