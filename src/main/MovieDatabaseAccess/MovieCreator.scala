package MovieDatabaseAccess

import java.io.{File, PrintWriter}
import java.util.NoSuchElementException


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



object run {




  def main(args: Array[String]) {
    val mov = new MovieCreator()
    val g = new GraphCreator(mov.Actors, mov.Genres, mov.titleMovie,mov.idMovie)
    val graph = g.preProcGraph()
    val reversedActors = mov.Actors map {_.swap}
    val reversedDirectors    = mov.Directors map {_.swap}
    while(true){
      println("What movies do you like?")
      println("")
      val sourceId = mov.findMovieId(1)
      println(sourceId)
      val destId = mov.findMovieId(2)
      println(destId)



      val sssp = g.createGraph(graph:Graph[Array[Double], Double],sourceId:Int)
      val path = g.findPath(sssp,destId,sourceId,mov.idMovie)

      val size = path.size
      println(path.iterator)
      path.remove(0)
      path.remove(size-2)
      val i = 2
      println("")
      println("Movie Recommendations: ")
      for(asd<-path){
        try
          println(mov.idMovie(asd.toString.toDouble.toInt).getTitle)
        catch {case noSuchElementException: NoSuchElementException => }
      }
      println("")
      println("Actor Recommendations: ")
      for(asd<-path){
        try
          println(reversedActors(asd.toString.toDouble.toInt))
        catch {case noSuchElementException: NoSuchElementException => }
      }
      println("")
      println("Director Recommendations: ")
      for(asd<-path){
        try
          println(reversedDirectors(asd.toString.toDouble.toInt))
        catch {case noSuchElementException: NoSuchElementException => }
      }


      println("")
      println("")


    }


    /*
    val format_sssp: RDD[String] = sssp.vertices.map(vertex =>
      "Vertex " + vertex._1 + ": distance is " + vertex._2(0) + ", previous node is Vertex " + vertex._2(1).toInt)
    format_sssp.collect.foreach(println(_))



    if (args.length > 2) {
      val outputFileDir = args(2)
      format_sssp.saveAsTextFile(outputFileDir)
    }*/

  }



}

object Done extends Exception{

}

object NotArray extends Exception{

}


 class GraphCreator(val actors:scala.collection.mutable.HashMap[String,Int], val genres:scala.collection.mutable.HashMap[String,Int], val movies:scala.collection.mutable.HashMap[String,Movie], val idmov:scala.collection.mutable.HashMap[Int,Movie]){



   System.setProperty("hadoop.home.dir", "D://KEX2016//Winutils");
   val configuration = new SparkConf()
     .setAppName("Basics")
     .setMaster("local")


   val sc = new SparkContext(configuration)

   val idMovies = idmov
   val Actors = actors
   val Genres = genres
   val Movies = movies
   val vertexArray: ArrayBuffer[(Long,Array[Double])] = ArrayBuffer()
   val edgeArray:ArrayBuffer[Edge[Double]] = ArrayBuffer()



  //val vertices:Array[(VertexId,(Int))]




// TODO: Should possible be done in earlier stage, how to map given ID in Actors -> VertexID?
  def preProcGraph(): Graph[Array[Double], Double] ={
  val pw = new PrintWriter(new File("graph.txt" ))
    for(movie<-this.Movies.values){
      vertexArray.append((movie.ID,Array(Double.PositiveInfinity,movie.ID)))
      val splitCast = movie.Cast.split(" ")
      for(cast<-splitCast)
        vertexArray.append((cast.toLong,Array(Double.PositiveInfinity,movie.ID)))
      val splitGenre = movie.Genre.split(" ")
      vertexArray.append((splitGenre(0).toLong,Array(Double.PositiveInfinity,movie.ID)))
      vertexArray.append((movie.Director.toLong,Array(Double.PositiveInfinity,movie.ID)))
      createEdges(movie,splitCast,splitGenre,pw)
    }


    val V = vertexArray.toArray
    val E = edgeArray.toArray


    val vertexRDD: RDD[(VertexId,Array[Double])] = sc.parallelize(V)
    val edgeRDD: RDD[Edge[Double]] = sc.parallelize(E)

    val graph = Graph(vertexRDD,edgeRDD)
  pw.close()
  return graph


  }
   def createGraph(graph:Graph[Array[Double], Double],sourceId:Int): Graph[Array[Double], Double]={

     val g = graph.mapVertices((id, _) =>
       if (id == sourceId) Array(0.0, id)
       else Array(Double.PositiveInfinity, id)
     )

     val sssp = g.pregel(Array(Double.PositiveInfinity, -1))(
       (id, dist, newDist) => {
         if (dist(0) < newDist(0)) dist
         else newDist
       },
       triplet => {
         if (triplet.srcAttr(0) + triplet.attr < triplet.dstAttr(0)) {
           Iterator((triplet.dstId, Array(triplet.srcAttr(0) + triplet.attr, triplet.srcId)))
         }
         else {
           Iterator.empty
         }
       },
       (a, b) => {
         if (a(0) < b(0)) a
         else b
       }
     )
     return sssp
   }



   def findPath(sssp:Graph[Array[Double], Double], destId:Int,sourceId:Int,idMovie: scala.collection.mutable.HashMap[Int, Movie]): ArrayBuffer[Any]={

     val path:ArrayBuffer[Any] = ArrayBuffer()
     val node = sssp.vertices.filter { case (id, _) => id == destId }.collect
     path.append(destId)
     var prev = node(0)._2(1)
     path.append(prev)
     while(prev !=sourceId ){
       val node = sssp.vertices.filter { case (id, _) => id == prev }.collect
       prev = node(0)._2(1)
       path.append(prev)

     }

     return path
   }

  //TODO: Link VertexId's
  def createEdges(movie:Movie,splitCast:Array[String],splitGenre:Array[String],pw:PrintWriter): Unit ={

      edgeArray.append(Edge(movie.ID,splitCast(0).toLong,2))
      pw.write(movie.ID + "\t" + splitCast(0) + "\t" + "1" + "\n")
      edgeArray.append(Edge(splitCast(0).toLong,movie.ID,2*(10- movie.getimdbRating.toDouble)))
      pw.write( splitCast(0).toLong + "\t" + movie.ID + "\t" + "1" + "\n")

      edgeArray.append(Edge(movie.ID,movie.Director.toLong,1))
      pw.write(movie.ID + "\t" + movie.Director + "\t" + "1" + "\n")
      edgeArray.append(Edge(movie.Director.toLong,movie.ID,1*(10- movie.getimdbRating.toDouble)))
      pw.write( movie.Director.toLong + "\t" + movie.ID + "\t" + "1" + "\n")


      edgeArray.append(Edge(movie.ID,splitGenre(0).toLong,100))
      pw.write(movie.ID + "\t" + splitGenre(0).toLong + "\t" + "3" + "\n")
      edgeArray.append(Edge(splitGenre(0).toLong,movie.ID,100*(10- movie.getimdbRating.toDouble)))
      pw.write( splitGenre(0).toLong + "\t" + movie.ID + "\t" + "3" + "\n")


    }








}




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
class Movie(list:Array[String], Id:Long){//, vertex: VertexId) {
  val ID     = Id
  val ImdbID = list(1)
  val Title = list(2)
  val Year = list(3)
  val Rating = list(4)
  val Runtime = list(5)
  var Genre = list(6)
  val Released = list(7)
  var Director = list(8)
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
  //val vertexId:VertexId = vertex

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
  def getID: Long ={
    ID
  }

}