package MovieDatabaseAcess
import java.util.NoSuchElementException

import MovieDatabaseAccess.MovieCreator
import org.apache.spark.graphx.Graph

import scala.collection.mutable.ArrayBuffer

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
      val movRecs:ArrayBuffer[String] = ArrayBuffer()
      val actRecs:ArrayBuffer[String] = ArrayBuffer()
      val dirRecs:ArrayBuffer[String] = ArrayBuffer()
      val size1 = path.size
      println(path.iterator)
      actRecs.append(reversedActors(mov.idMovie(path(size1-1).toString.toDouble.toInt).getCast.split(" ")(0).toInt))

      val sourceID_Three = mov.idMovie(path(0).toString.toDouble.toInt).getDirector.split(" ")(0).toInt
      val sssp3 = g.createGraph(graph:Graph[Array[Double], Double],sourceID_Three:Int)
      val path3 = g.findPath(sssp3,destId,sourceID_Three,mov.idMovie)
      val size3 = path3.size
      path.remove(0)
      path.remove(size1-2)
      path3.remove(0)

      try{
      val sourceID_Two = mov.idMovie(path(0).toString.toDouble.toInt).getCast.split(" ")(1).toInt
      val sssp2 = g.createGraph(graph:Graph[Array[Double], Double],sourceID_Two:Int)
      val path2 = g.findPath(sssp2,destId,sourceID_Two,mov.idMovie)
      val size2 = path2.size
      path2.remove(0)
      for(a<-path2)
        path.append(a)
      }catch{
        case e: ArrayIndexOutOfBoundsException =>
        case e: NoSuchElementException =>}
      for(b<-path3)
        path.append(b)

      try{
        val sourceID_Four = mov.idMovie(path(0).toString.toDouble.toInt).getCast.split(" ")(2).toInt
        val sssp4 = g.createGraph(graph:Graph[Array[Double], Double],sourceID_Four:Int)
        val path4 = g.findPath(sssp4,destId,sourceID_Four,mov.idMovie)
        val size4 = path4.size
        path4.remove(0)
        for(c<-path4)
          path.append(c)
      }catch{
        case e: ArrayIndexOutOfBoundsException =>
        case e: NoSuchElementException =>}

      val i = 2


      for(asd<-path){
        try{
          movRecs.append(mov.idMovie(asd.toString.toDouble.toInt).getTitle)
 }
        catch {

          case noSuchElementException: NoSuchElementException =>
          case e: ArrayIndexOutOfBoundsException => }
        try{
          actRecs.append(reversedActors(mov.idMovie(asd.toString.toDouble.toInt).getCast.split(" ")(1).toInt))
        }catch {

          case noSuchElementException: NoSuchElementException =>
          case e: ArrayIndexOutOfBoundsException =>
        }
      }

      for(asd<-path){
        try
          actRecs.append(reversedActors(asd.toString.toDouble.toInt))
        catch {case noSuchElementException: NoSuchElementException => }
      }

      for(asd<-path){
        try
          dirRecs.append(reversedDirectors(asd.toString.toDouble.toInt))
        catch {case noSuchElementException: NoSuchElementException => }
      }
      //for(a<-path)
        //println(a)
      val used: ArrayBuffer[String] = ArrayBuffer()
      println("")
      println("Movie Recommendations: ")
      movRecs.reverse
      for(movs<-movRecs){
        if(!(used contains movs))
          println(movs)
          used.append(movs)
      }
      println("")
      println("Actor Recommendations: ")
      actRecs.reverse
      for(acts<-actRecs){
        if(!(used contains acts))
          println(acts)
        used.append(acts)
    }
      println("")
      println("Director Recommendations: ")
      dirRecs.reverse
      for(dirs<-dirRecs){
        if(!(used contains dirs))
          println(dirs)
          used.append(dirs)
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









