package image

import java.awt.image.BufferedImage	
import java.awt.Rectangle

object ImageUtil {
    
  def blit(img: BufferedImage, rect: Rectangle): Array[Int] = {
    // TODO - Check the rectangle can fit.
    val data = new Array[Int](rect.width * rect.height)
    img.getRGB(rect.x, rect.y, rect.width, rect.height, data, 0, rect.width)
    data
  }
  
  def savePng(img: BufferedImage, file: java.io.File): Unit = {
    javax.imageio.ImageIO.write(img, "png", file)
  }
}