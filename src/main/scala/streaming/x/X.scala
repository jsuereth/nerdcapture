package streaming
package x


import scala.sys.process.Process

object X {
  case class Window(id: Int, name: String)
  object XWindowLine {
    val pattern = java.util.regex.Pattern.compile("""\s+(0x[0-9a-f]+)\s+"([^"]+)".*""")
    def unapply(line: String): Option[(Int, String)] = try {
      val m = pattern.matcher(line)
      if(m.matches) Some(Integer.parseInt(m.group(1).drop(2), 16), m.group(2))
      else None
    } catch {
      case e: NumberFormatException => None
    }
  }
  def currentWindows: Seq[Window] = {
    import sys.process._
    for {
      XWindowLine(id, name) <- Process("xwininfo -root -tree").lines
    } yield Window(id,name)
  }
  def windowId(name: String): Option[Int] =
    currentWindows find { win =>
      win.name.toLowerCase contains name.toLowerCase
    } map (_.id)
}