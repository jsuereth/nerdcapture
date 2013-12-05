package video

import play.api.libs.iteratee._
import java.awt.image.BufferedImage
import java.awt.{Rectangle, Color}
import capture.ScreenCapture
import concurrent.ExecutionContext
import java.io.File
import javax.imageio.ImageIO
import org.imgscalr.Scalr
import akka.actor.{Actor, ActorRefFactory, Props}

case class Layout(size: Rectangle, items: Seq[LayoutItem])
case class LayoutItem(source: Enumerator[ScreenCapture], location: Rectangle)

case class LayoutFrameRequestItem(id: Int, capture: ScreenCapture, location: Rectangle) {
  override def equals(o: Any): Boolean = o match {
    case other: LayoutFrameRequestItem => other.id == id
    case _ => false
  }
  override def hashCode: Int = id
}
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
  
  // TODO - We should not continually send messages of the overlay.
  // Also, we should be able to swap layout midstream based on user-input...
  // Also, we should be able to enable/disable driving a particular stream.
  def makeOverlay(layout: Layout)(implicit ex: ExecutionContext, factory: ActorRefFactory): Enumerator[ScreenCapture] = {
    val fixedEnums: Seq[Enumerator[LayoutFrameRequestItem]] = 
      for {
        (LayoutItem(source, location), idx) <- layout.items.zipWithIndex
        resized = source &> ImageUtils.resize(location.width, location.height)
      } yield resized.map { screen => LayoutFrameRequestItem(idx, screen, location) }
    
    val (enumerator, channel) = Concurrent.broadcast[ScreenCapture]
    val handler = factory.actorOf(Props(new FrameConstructingActor(layout.size, layout.items.size, channel)))
    // Now start driving the enumerators
    def driveComponent(e: Enumerator[LayoutFrameRequestItem]): Unit = {
      concurrent.Future(e apply Iteratee.foreach { request =>
        handler ! request
      })
    }
    fixedEnums foreach driveComponent
    enumerator
  }  
}

class FrameConstructingActor(layoutSize: Rectangle, numComponents: Int, frameChannel: Concurrent.Channel[ScreenCapture]) extends Actor {
  var constructing = emptyFrame
  def emptyFrame = collection.mutable.ArrayBuffer.empty[LayoutFrameRequestItem]
  
  
  // TODO - Receive timeout?
  def receive: Receive = {
    case e: LayoutFrameRequestItem =>
      addToFrame(e)
      if(isFrameFinished) sendCurrentFrame()
    case any =>
      println("Unknown msg: " + any)
  }
  
  def addToFrame(e: LayoutFrameRequestItem): Unit = {
    println(s"Adding component ${e.id} to frame")
    // Drop previous recording of the same value.
    constructing.indexOf(e) match {
      case -1 => constructing.append(e)
      case n => constructing.update(n, e)
    }
  }
  
  def isFrameFinished(): Boolean =
    constructing.size >= numComponents
  
  def makeFrame: LayoutFrameRequest = {
    // Here we need to order the components by z-index
     LayoutFrameRequest(layoutSize, constructing.sortBy(_.id))
  }
    
  def sendCurrentFrame(): Unit = {
    println(s"Sending next frame with components: ${constructing.map(_.id).mkString("[",",","]")}")
    val screen = Overlays.layoutFrame(makeFrame)
    frameChannel.push(screen)
    constructing = emptyFrame
  }
}

