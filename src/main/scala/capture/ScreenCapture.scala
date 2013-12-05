package capture

import java.awt.Rectangle
import java.awt.image.BufferedImage	
import play.api.libs.iteratee.Enumerator
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** Capture of the current screen and timestamp when it happened. */
case class ScreenCapture(screen: BufferedImage, timestamp: Long = System.currentTimeMillis)