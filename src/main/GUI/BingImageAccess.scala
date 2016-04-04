package GUI
import net.ettinsmoor.Bingerator

import scalaj.http.{HttpResponse, Http}

/**
  * Created by Carl-Johan on 2016-04-04.
  */


class BingImageAccess {

  val appkey = "CLuHHwd16HNuyI5XhwnqqQoaQkt1ijO1JSB8xjICSu4"


  def getImage(name:String): String ={
    val results1 = new Bingerator(appkey).SearchImages(name).take(1)
    return results1(0).media_url

  }


}
