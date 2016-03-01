import scala.collection.immutable
import scala.reflect.io.File
import scala.io.Source
import scala.util.control.Breaks._
import spray.json._
object run {

  def main(args: Array[String]) {
    val mov=new MovieCreator()

  }
}

object Done extends Exception{

}

class MovieCreator(){
  val list: List[String] = List("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19")
  //for line in TSV.line
  //list = line.spltOn(TAB)
  //dict.add(list(0),Movie(list)
  var i:Int = 0
  var key1:String = ""
  var key2:String = ""
  var map = scala.collection.mutable.HashMap.empty[String,Movie]
  for(line <- Source.fromFile("omdbMovies.txt","iso-8859-1").getLines()){
    val liist = line.split("\t")
    //var list1:List[String] = List[String]()
    //list1 ++ line.split("\t")
    i=i+1
    //if(i==10)
    // print(liist(2))
    try map += (liist(2)-> new Movie(liist)) catch{case ioob: IndexOutOfBoundsException => print(ioob)}

  }
  //print(map.size)





  try {
    for (key <- map.keys) {
      if((key contains "Batman") && !(key1 contains "Batman"))
        key1 = key
      if((key contains "Lord of") && !(key2 contains "Making"))
        key2 = key
      if((key1 contains "Batman") && (key2 contains "Lord of") ) throw Done }
  } catch {
    case Done =>
  }
  val time1:Long =  System.currentTimeMillis()
  val a:Movie = map("Miss Jerry")
  print("Title: \n" +a.getTitle() + "\n" + "Genre: \n"+ a.getGenre() + "\n"+"Plot: \n" + a.getPlot())
  print("\n\n")
  val b:Movie = map(key1)
  print("Title: " +b.getTitle() + "\n" + "Genre: "+ b.getGenre() + "\n" +"Plot: \n" + b.getPlot())
  print("\n\n")
  val c:Movie = map(key2)
  print("Title: " +c.getTitle() + "\n" + "Genre: "+ c.getGenre() + "\n" +"Plot: \n" + c.getPlot())
  print("\n\n")

  print(System.currentTimeMillis()-time1)
  //def main(args: Array[String]) {
  //  val mov = new MovieCreator()
  //}
}

class Movie(list:Array[String]) {
  val ID     = list(0)
  val ImdbID = list(1)
  val Title = list(2)
  val Year = list(3)
  val Rating = list(4)
  val Runtime = list(5)
  val Genre = list(6)
  val Released = list(7)
  val Director = list(8)
  val Writer = list(9)
  val Cast = list(10)
  val Metacritic = list(11)
  val imdbRating = list(12)
  val imdbVotes = list(13)
  val Poster = list(14)
  val Plot = list(15)
  val FullPlot = list(16)
  val Language = list(17)
  val Country = list(18)
  val Awards = list(19)
  val lastUpdated = list(20)

  def getTitle(): String ={
    return Title
  }
  def getGenre(): String ={
    return Genre
  }
  def getPlot(): String ={
    return Plot
  }
  def getYear(): String ={
    return Year
  }
  def getCast(): String ={
    return Cast
  }
  def getImdbRating(): String ={
    return imdbRating
  }

}