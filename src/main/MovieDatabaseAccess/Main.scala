package MovieDatabaseAcess
import java.util.NoSuchElementException

import MovieDatabaseAccess.MovieCreator
import org.apache.spark.graphx.Graph

object Main {




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









