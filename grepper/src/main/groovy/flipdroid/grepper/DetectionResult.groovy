package flipdroid.grepper

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2/27/11
 * Time: 7:52 PM
 * To change this template use File | Settings | File Templates.
 */
class DetectionResult {
  def titleNode
  def titleText
  def bodyText
  def bodyNode

  public String getTitleText(){
    return titleText?.trim();
  }

  public String getBodyText(){
    return bodyText;
  }
}
