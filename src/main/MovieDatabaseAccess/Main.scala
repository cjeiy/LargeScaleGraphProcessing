package MovieDatabaseAcess
import java.util.NoSuchElementException

import GUI.{ViewTest, Graphics}
import MovieDatabaseAccess.MovieCreator
import org.apache.spark.graphx.Graph

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object Main {
  val v:ViewTest = new ViewTest
  val mov: MovieCreator = new MovieCreator(v:ViewTest)
  val g: GraphCreator = new GraphCreator(mov.Actors, mov.Genres, mov.titleMovie,mov.idMovie)
  val graph: Graph[Array[Double], Double] = g.preProcGraph()


  def buildNewPath(sourceId:Int,mov:MovieCreator,g:GraphCreator,graph:Graph[Array[Double], Double],destId:Int): (ArrayBuffer[Any],Int) ={
    var path = ArrayBuffer.empty[Any]
    var oldPathZ = 0
    try{
      val sssp = g.createGraph(graph:Graph[Array[Double], Double],sourceId:Int)
      path = g.findPath(sssp,destId,sourceId,mov.idMovie)
      oldPathZ = path(0).toString.toInt
      path.remove(0)
    }catch{
      case e: ArrayIndexOutOfBoundsException =>
      case e: NoSuchElementException =>}

    return (path, oldPathZ)
  }

  def splitRecommendations(mov:MovieCreator,path:ArrayBuffer[Any],
                           reversedActors:mutable.HashMap[Int, String],
                           reversedDirectors:mutable.HashMap[Int, String]): (ArrayBuffer[String],ArrayBuffer[String],ArrayBuffer[String]) ={

    val movRecs:ArrayBuffer[String] = ArrayBuffer()
    val actRecs:ArrayBuffer[String] = ArrayBuffer()
    val dirRecs:ArrayBuffer[String] = ArrayBuffer()


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


    return (movRecs,dirRecs,actRecs)
  }




  def graphToPathX(g:GraphCreator,mov:MovieCreator,graph:Graph[Array[Double], Double],sourceIds:Int, destIds:Int): (ArrayBuffer[String],ArrayBuffer[String],ArrayBuffer[String]) ={
    val reversedActors: mutable.HashMap[Int, String] = mov.Actors map {_.swap}
    val reversedDirectors    = mov.Directors map {_.swap}
    val path:ArrayBuffer[Any] = ArrayBuffer()
    var sourceId = sourceIds
    val destId = destIds

    val ret = buildNewPath(sourceId,mov,g,graph,destId)
    path ++= ret._1
    val size = path.size
    path.remove(size-1)
    val oldZ = ret._2

    sourceId = mov.idMovie(oldZ.toString.toDouble.toInt).getCast.split(" ")(1).toInt
    path ++= buildNewPath(sourceId, mov, g, graph, destId)._1

    sourceId = mov.idMovie(oldZ.toString.toDouble.toInt).getDirector.split(" ")(0).toInt
    path ++= buildNewPath(sourceId, mov, g, graph, destId)._1

    sourceId = mov.idMovie(oldZ.toString.toDouble.toInt).getCast.split(" ")(2).toInt
    path ++= buildNewPath(sourceId, mov, g, graph, destId)._1

    val recs = splitRecommendations(mov,path,reversedActors,reversedDirectors)
    val movRecs = recs._1
    val dirRecs = recs._2
    val actRecs = recs._3

    return (movRecs,dirRecs,actRecs)

  }

  def graphRun(mov1:String, mov2:String): (ArrayBuffer[String],ArrayBuffer[String],ArrayBuffer[String]) ={
    val mov1Id = mov.titleMovie(mov1).ID.toInt
    val mov2Id = mov.titleMovie(mov2).ID.toInt
    val ret = graphToPathX(g,mov,graph,mov1Id,mov2Id)

    val movRecs = ret._1
    val dirRecs = ret._2
    val actRecs = ret._3

    return (movRecs,actRecs,dirRecs)

  }

  def graphToPathText(g:GraphCreator,
                      mov:MovieCreator,
                      graph:Graph[Array[Double], Double]): Unit ={

    val reversedActors: mutable.HashMap[Int, String] = mov.Actors map {_.swap}
    val reversedDirectors    = mov.Directors map {_.swap}





    while(true){

      val path:ArrayBuffer[Any] = ArrayBuffer()


      println("What movies do you like?")
      println("")
      var sourceId = mov.findMovieId(1)
      println(sourceId)
      val destId = mov.findMovieId(2)
      println(destId)

      val ret = buildNewPath(sourceId,mov,g,graph,destId)
      path ++= ret._1
      val size = path.size
      path.remove(size-1)
      val oldZ = ret._2

      sourceId = mov.idMovie(oldZ.toString.toDouble.toInt).getCast.split(" ")(1).toInt
      path ++= buildNewPath(sourceId, mov, g, graph, destId)._1

      sourceId = mov.idMovie(oldZ.toString.toDouble.toInt).getDirector.split(" ")(0).toInt
      path ++= buildNewPath(sourceId, mov, g, graph, destId)._1

      sourceId = mov.idMovie(oldZ.toString.toDouble.toInt).getCast.split(" ")(2).toInt
      path ++= buildNewPath(sourceId, mov, g, graph, destId)._1


      //actRecs.append(reversedActors(mov.idMovie(path(size1-1).toString.toDouble.toInt).getCast.split(" ")(0).toInt))


      val recs = splitRecommendations(mov,path,reversedActors,reversedDirectors)
      val movRecs = recs._1
      val dirRecs = recs._2
      val actRecs = recs._3



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


      path--=path
      movRecs--=movRecs
      dirRecs--=dirRecs
      actRecs--=actRecs


    }



  }

  def getMov(): MovieCreator ={
    return this.mov
  }




  def main(args: Array[String]){




    v.main(Array(""))
    val reversedActors: mutable.HashMap[Int, String] = mov.Actors map {_.swap}
    val reversedDirectors: mutable.HashMap[Int, String] = mov.Directors map {_.swap}

    //println("For GUI press 1, for console press 2")
    //val choice = Console.readLine
    //if(choice == "1")

    //if (choice == "2")
      //graphToPathText(g,mov,graph)




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









