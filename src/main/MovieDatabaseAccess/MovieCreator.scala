package MovieDatabaseAccess

import java.util.NoSuchElementException

import org.apache.spark.graphx.VertexId

import scala.io.Source

object run {

  def main(args: Array[String]) {
    val mov=new MovieCreator()
    val graph=new GraphCreator(mov.Actors, mov.Genres, mov.map)
  }
}

object Done extends Exception{

}

object NotArray extends Exception{

}


 class GraphCreator(val actors:scala.collection.mutable.HashMap[String,Int], val genres:scala.collection.mutable.HashMap[String,Int], val movies:scala.collection.mutable.HashMap[String,Movie]){

  val Actors = actors
  val Genres = genres
  val Movies = movies
  //val vertices:Array[(VertexId,(Int))]




// TODO: Should possible be done in earlier stage, how to map given ID in Actors -> VertexID?
  def createVertices(): Unit ={
    for(actor<-this.Actors.keys){

    }
    

  }


  //TODO: Link VertexId's
  def createEdges(): Unit ={


  }



}



class MovieCreator() {
  var map = scala.collection.mutable.HashMap.empty[String, Movie]
  var Genres = scala.collection.mutable.HashMap.empty[String, Int]
  var Actors = scala.collection.mutable.HashMap.empty[String, Int]
  var i: Int = 0
  var key1: String = ""
  var key2: String = ""
  addMovieAttributes()
  splitAndIndexize()


  def addMovieAttributes(): Unit = {
  for (line <- Source.fromFile("src\\main\\resources\\omdbMovies.txt", "iso-8859-1").getLines()) {
    val liist = line.split("\t")

    try{
        if((liist(12).toDouble>5)&&(liist(13).toDouble>1000)) // IMDB RATING / VOTE
          this.map += (liist(2) -> new Movie(liist,))}
    catch {
      case ioob: IndexOutOfBoundsException => print(ioob)
      case nfe: NumberFormatException => //if (!(liist(12).isEmpty) || (!(liist(13).isEmpty ))){print(nfe)}
    }
    }
  }

  def splitAndIndexize(): Unit = {
    var j = 30
    var j2 = 0
    for (movie <- map.values) {
        val splitGenre = movie.Genre.split(", ")
        //print(splitGenre(0))
        movie.Genre = ""
        for (i <- 0 to splitGenre.size - 1) {
          var index = 0
          try{
            index = Genres(splitGenre(i))
            movie.Genre = movie.Genre + index + " "
          } catch{case noSuchElementException: NoSuchElementException =>
            this.Genres += (splitGenre(i) -> j2)
            movie.Genre = movie.Genre + j2 + " "
            j2 += 1}
        }
            val splitCast = movie.Cast.split(", ")
            movie.Cast = ""
            for (i <- 0 to splitCast.size - 1) {
                var index = 0
                try{
                  index = Actors(splitCast(i))
                  movie.Cast = movie.Cast + index + " "
                } catch{case noSuchElementException: NoSuchElementException =>

                  this.Actors += (splitCast(i) -> j)
                  movie.Cast = movie.Cast + j + " "
                  j += 1
                  }

              }
      }
    }



  print(map.keys.size)
  ///### Pre-Proc TESTPRINTING ###
  try {
    for (key <- map.keys) {
      if ((key contains "Batman") && !(key1 contains "Batman Begins"))
        key1 = key
      if ((key contains "Lord of") && !(key2 contains "Making"))
        key2 = key
      if ((key1 contains "Batman Begins") && (key2 contains "Lord of")) throw Done
    }
  } catch {
    case Done =>
  }

  ///###TESTPRINTING###
  val time1: Long = System.currentTimeMillis()
  try{val a: Movie = map("Miss Jerry")
  print("Title: \n" + a.getTitle + "\n" + "Genre: \n" + a.getGenre + "\n" + "Director: \n" + a.getDirector+ "\n" +"Cast: \n" + a.getCast)
  print("\n\n")}
  catch {case noSuchElementException: NoSuchElementException => }
  try{
  val b: Movie = map(key1)
  print("Title: " + b.getTitle + "\n" + "Genre: " + b.getGenre + "\n" + "Director: \n" + b.getDirector+ "\n" +"Cast: \n" + b.getCast)
  print("\n\n")}
  catch {case noSuchElementException: NoSuchElementException => }
  val c: Movie = map(key2)
  print("Title: " + c.getTitle + "\n" + "Genre: " + c.getGenre + "\n" + "Director: \n" + c.getDirector + "\n" +"Cast: \n" + c.getCast)
  print("\n\n")
  for(i <- Genres)
    print(i + "\n")

  print("Amount of Actors: " + this.Actors.keys.size + "\n")
  print("Amount of Genres: " + this.Genres.keys.size +"\n")
  print("Time for accessing 3 items: "+ (System.currentTimeMillis() - time1) + "ms")
}

///FIRST APPROACH
class Movie(list:Array[String], vertex: VertexId) {
  val ID     = list(0)
  val ImdbID = list(1)
  val Title = list(2)
  val Year = list(3)
  val Rating = list(4)
  val Runtime = list(5)
  var Genre = list(6)
  val Released = list(7)
  val Director = list(8)
  val Writer = list(9)
  var Cast = list(10)
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
  val vertexId:VertexId = vertex

  def getTitle: String ={
     Title
  }
  def getGenre: String ={
     Genre
  }
  def getPlot: String ={
     Plot
  }
  def getYear: String ={
     Year
  }
  def getCast: String ={
     Cast
  }
  def getimdbRating: String ={
     imdbRating
  }
  def getDirector: String ={
    Director
  }
  def getWriter: String ={
    Writer
  }

}