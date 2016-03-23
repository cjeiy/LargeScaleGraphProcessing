package MovieDatabaseAccess

import java.io.{File, PrintWriter}
import java.util.NoSuchElementException


import MovieDatabaseAcess.{Done, Movie}
import org.apache.spark.graphx.util.GraphGenerators
import org.apache.spark.util.collection.{Sorter, PrimitiveVector}
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.graphx.{GraphLoader, Graph, Edge, VertexId}

import scala.collection.mutable.ArrayBuffer
import scala.io.Source


import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD

import org.apache.spark.storage.StorageLevel
import org.apache.spark.{Logging, SparkContext}
import org.apache.spark.graphx.impl.{EdgePartitionBuilder, GraphImpl}




class MovieCreator() {
  var idMovie = scala.collection.mutable.HashMap.empty[Int, Movie]
  var titleMovie = scala.collection.mutable.HashMap.empty[String, Movie]
  var Genres = scala.collection.mutable.HashMap.empty[String, Int]
  var Actors = scala.collection.mutable.HashMap.empty[String, Int]
  var Directors = scala.collection.mutable.HashMap.empty[String, Int]
  var i: Int = 0
  var id = 0
  var key1: String = ""
  var key2: String =""
  addMovieAttributes()
  splitAndIndexize()


  def addMovieAttributes(): Unit = {
  for (line <- Source.fromFile("src\\main\\resources\\omdbMovies.txt", "iso-8859-1").getLines()) {
    val liist = line.split("\t")

    try{
        if((liist(12).toDouble>5)&&(liist(13).toDouble>1000)) // IMDB RATING / VOTE

          this.titleMovie += (liist(2) -> new Movie(liist,id.toLong))
          this.idMovie += (id -> new Movie(liist,id.toLong))
          id = id+1

    }
    catch {
      case ioob: IndexOutOfBoundsException => print(ioob)
      case nfe: NumberFormatException => //if (!(liist(12).isEmpty) || (!(liist(13).isEmpty ))){print(nfe)}
    }
    }
  }

  def splitAndIndexize(): Unit = {
    var j = 30 +id
    var j2 = 0 +id
    for (movie <- titleMovie.values) {
        val splitGenre = movie.Genre.split(", ")
        //print(splitGenre(0))
        movie.Genre = ""
        for (i <- 0 until splitGenre.size) {
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
            for (i <- 0 until splitCast.size) {
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
      val splitDir = movie.Director.split(", ")
      try{
        var index = Directors(splitDir(0))
        movie.Director = index.toString
      } catch{case noSuchElementException: NoSuchElementException =>
        j += 1
        this.Directors += (splitDir(0) -> j)
        movie.Director =  j.toString

      }




      }
    }

  def findMovieId(a:Int): Int ={
    var mov1Id = ArrayBuffer.empty[(String,Long)]
    println("Movie "+ a+ ": ")
    println("")
    while(mov1Id.isEmpty){
      val mov1 = Console.readLine
      mov1Id = this.lookUpMovie(mov1)
      if (mov1Id.isEmpty)
        println("Movie Could not be found. Try a different format. E.g. Starwars -> Star Wars")
    }
    var i:Int = 1
    for(movies<-mov1Id) {
      println(i.toString+": "+movies._1+"; ID: " + movies._2)
      i+=1
    }
    val m1:String = Console.readLine
    val choosenMov1 = mov1Id(m1.toInt-1)
    val Id= choosenMov1._2.toInt
    return Id

  }


  def lookUpMovie(movieString:String):ArrayBuffer[(String,Long)]={
    val movieAndId: ArrayBuffer[(String,Long)] = ArrayBuffer()
      for(movie<-titleMovie.values){
        if(movie.Title.toLowerCase contains movieString.toLowerCase)
          movieAndId.append((movie.Title,movie.ID))
        }
     movieAndId

      }




  print(titleMovie.keys.size)
  ///### Pre-Proc TESTPRINTING ###
  try {
    for (key <- titleMovie.keys) {
      if ((key contains "Batman") && !(key1 contains "Batman Begins"))
        key1 = key
      if ((key contains "Lord of") && !(key2 contains "Making"))
        key2 = key
     // if ((key1._1 contains "Batman Begins") && (key2._1 contains "Lord of")) throw Done
    }
  } catch {
    case Done =>
  }

  ///###TESTPRINTING###
  val time1: Long = System.currentTimeMillis()
  try{
  val b: Movie = titleMovie(key1)
  print("Title: " + b.getTitle + "\n" + "Genre: " + b.getGenre + "\n" + "Director: \n" + b.getID+ "\n" +"Cast: \n" + b.getCast)
  print("\n\n")}
  catch {case noSuchElementException: NoSuchElementException => }

  val c: Movie = titleMovie(key2)
  print("Title: " + c.getTitle + "\n" + "Genre: " + c.getGenre + "\n" + "Director: \n" + c.getID + "\n" +"Cast: \n" + c.getCast)
  print("\n\n")


  for(i <- Genres)
    print(i + "\n")

  print("Amount of Actors: " + this.Actors.keys.size + "\n")
  print("Amount of Genres: " + this.Genres.keys.size +"\n")
  print("Time for accessing 3 items: "+ (System.currentTimeMillis() - time1) + "ms")
}

///FIRST APPROACH
