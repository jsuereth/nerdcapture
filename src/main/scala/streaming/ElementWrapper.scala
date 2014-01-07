package streaming

import org.gstreamer.Element

trait ElementWrapper {
  def underlying: Element
}
object ElementWrapper {
  implicit def unwrap(e: ElementWrapper): Element = e.underlying
}