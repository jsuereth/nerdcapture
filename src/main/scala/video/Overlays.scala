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
import rx.lang.scala._
import rx.lang.scala.Subject

// TODO - layouts should actually just be dynamic...


case class Layout(size: Rectangle, items: Seq[LayoutItem])

trait LayoutItem
case class LayoutImage(image: BufferedImage, location: Rectangle) extends LayoutItem
case class LayoutStreamItem(source: Observable[ScreenCapture], location: Rectangle) extends LayoutItem

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
  
  def rxMakeOverlay(layout: Layout)(implicit actorFactory: ActorRefFactory): Observable[ScreenCapture] = {
    val actor = actorFactory.actorOf(Props(new OverlayActor()))
    val obsWrapper = Observable[ScreenCapture](rx.Observable.create(new rx.Observable.OnSubscribeFunc[ScreenCapture] {
      override def onSubscribe(t1: rx.Observer[_ >: ScreenCapture]): rx.Subscription = {
        actor ! AddOverlayListener(t1)
        // TODO - Return something that can unsubscribe...
        new rx.Subscription {
          def unsubscribe(): Unit = 
            actor ! RemoveOverlayListener(t1)
        }
      }
    }))
    actor ! layout
    obsWrapper
  }
  
}

case class AddOverlayListener(l: rx.Observer[_ >: ScreenCapture])
case class RemoveOverlayListener(l: rx.Observer[_ >: ScreenCapture])
class OverlayActor() extends Actor {
  
  // State related to the current layout.
  var listeners = Seq.empty[rx.Observer[_ >: ScreenCapture]]
  var layout: Option[Layout] = None
  var staticItems = Seq.empty[LayoutFrameRequestItem]
  var subscriptions = Seq.empty[Subscription]
  var numStreams: Int = 0
  
  // Frame we are constructing
  var constructing = collection.mutable.ArrayBuffer.empty[LayoutFrameRequestItem]
  var lastFrameTime = 0L
  var averageFrameTime = 0d
  var frameRate = 0d
  
  
  
  def receive: Receive = {
    case AddOverlayListener(o) => listeners +:= o
    case RemoveOverlayListener(o) => listeners = listeners.filterNot(_ == o)
    case layout: Layout => setupLayout(layout)
    case item: LayoutFrameRequestItem =>
      addToFrame(item)
      if(isFrameFinished) sendCurrentFrame()
      
    // TODO - Close this down after stream is done...
  }
  
  def addToFrame(e: LayoutFrameRequestItem): Unit = {
    // Drop previous recording of the same value.
    constructing.indexOf(e) match {
      case -1 => constructing.append(e)
      case n => constructing.update(n, e)
    }
  }
  def isFrameFinished(): Boolean =
    constructing.size >= numStreams
  
  def sendFrame(screen: ScreenCapture): Unit = {
    listeners foreach (_.onNext(screen))    
  }
  
  def updateFrameRate(): Unit = {
    val current = System.currentTimeMillis
    val time = current - lastFrameTime
    if(time < 10000L) {
      if(averageFrameTime < 0.001) averageFrameTime = time
      else averageFrameTime = (averageFrameTime + time) / 2.0
    }
    frameRate = 1000.0 / averageFrameTime
    lastFrameTime = current
  }
    
  def sendCurrentFrame(): Unit = {
    updateFrameRate()
    println(s"Sending next frame with frameRate: ${frameRate}")
    val screen = Overlays.layoutFrame(makeFrame)
    sendFrame(screen)
    constructing = collection.mutable.ArrayBuffer.empty[LayoutFrameRequestItem]
  }
    
  def makeFrame: LayoutFrameRequest = {
    // Here we need to order the components by z-index
    val components = staticItems ++ constructing
    LayoutFrameRequest(layout.get.size, components.sortBy(_.id))
  }
  
  def setupLayout(newLayout: Layout): Unit = {
    // TODO - Check to see if the layouts are different.
	subscriptions foreach (_.unsubscribe())
	constructing = collection.mutable.ArrayBuffer.empty[LayoutFrameRequestItem]
    layout = Some(newLayout)
    staticItems = 
      for {
        (LayoutImage(image, location), idx) <- newLayout.items.zipWithIndex
      } yield LayoutFrameRequestItem(idx, ScreenCapture(image), location)
    val streams =
     for {
        (LayoutStreamItem(stream, location), idx) <- newLayout.items.zipWithIndex
      } yield stream map { screen =>
        LayoutFrameRequestItem(idx, screen, location)
      }
    subscriptions = streams map { stream =>
      stream subscribe { item => self ! item }  
    }
    numStreams = subscriptions.size
  }
  
}