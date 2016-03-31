package MovieDatabaseAcess

import java.io.{File, PrintWriter}

import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkContext, SparkConf}

import scala.collection.mutable.ArrayBuffer

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

    edgeArray.append(Edge(movie.ID,splitCast(0).toLong,2*(5- movie.getimdbRating.toDouble/2)))
    pw.write(movie.ID + "\t" + splitCast(0) + "\t" + "1" + "\n")
    edgeArray.append(Edge(splitCast(0).toLong,movie.ID,2*(5- movie.getimdbRating.toDouble/2)))
    pw.write( splitCast(0).toLong + "\t" + movie.ID + "\t" + "1" + "\n")

    edgeArray.append(Edge(movie.ID,movie.Director.toLong,1*(5- movie.getimdbRating.toDouble/2)))
    pw.write(movie.ID + "\t" + movie.Director + "\t" + "1" + "\n")
    edgeArray.append(Edge(movie.Director.toLong,movie.ID,1*(5- movie.getimdbRating.toDouble/2)))
    pw.write( movie.Director.toLong + "\t" + movie.ID + "\t" + "1" + "\n")


    edgeArray.append(Edge(movie.ID,splitGenre(0).toLong,6*(5- movie.getimdbRating.toDouble/2)))
    pw.write(movie.ID + "\t" + splitGenre(0).toLong + "\t" + "3" + "\n")
    edgeArray.append(Edge(splitGenre(0).toLong,movie.ID,6*(5- movie.getimdbRating.toDouble/2)))
    pw.write( splitGenre(0).toLong + "\t" + movie.ID + "\t" + "3" + "\n")


  }








}