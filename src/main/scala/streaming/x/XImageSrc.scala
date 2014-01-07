package streaming
package x

import org.gstreamer._


class XImageSrc private(override val underlying: Element) extends ElementWrapper {
  def displayName: String =
    underlying.get("display-name").asInstanceOf[String]
  def displayName_=(value: String): Unit =
    underlying.set("display-name", value)
  def screenNum: Int =
    underlying.get("screen-num").asInstanceOf[Int]
  def screenNum_=(value: Int): Unit =
    underlying.set("screen-num", value)
  def showPointer: Boolean =
    underlying.get("show-pointer").asInstanceOf[Boolean]
  def showPointer_=(value: Boolean): Unit =
    underlying.set("show-pointer", value)
  def xid: Long =
    underlying.get("xid").asInstanceOf[Long]
  def xid_=(value: Long): Unit =
    underlying.set("xid", value)
  def xname: String =
    underlying.get("xname").asInstanceOf[String]
  def xname_=(value: String): Unit =
    underlying.set("xname", value)
  def startx: Int =
    underlying.get("startx").asInstanceOf[Int]
  def startx_=(value: Int): Unit =
    underlying.set("startx", value)
  def endx: Int =
    underlying.get("endx").asInstanceOf[Int]
  def endx_=(value: Int): Unit =
    underlying.set("endx", value)
  def starty: Int =
    underlying.get("starty").asInstanceOf[Int]
  def starty_=(value: Int): Unit =
    underlying.set("starty", value)
  def endy: Int =
    underlying.get("endy").asInstanceOf[Int]
  def endy_=(value: Int): Unit =
    underlying.set("endy", value)
    
  def useDamage: Boolean =
    underlying.get("use-damage").asInstanceOf[Boolean]
  def useDamage_=(value: Boolean): Unit =
    underlying.set("use-damage", value)
}
object XImageSrc extends IncreasingCount {
  def apply(): XImageSrc =
    new XImageSrc(ElementFactory.make("ximagesrc", s"ximagesrc-${nextCount}"))
}