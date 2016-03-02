package GraphXPrep

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD

/**
  * Created by Carl-Johan on 2016-03-01.
  */
object GraphIntro {

  System.setProperty("hadoop.home.dir", "D://KEX2016//Winutils");
  val configuration = new SparkConf()
    .setAppName("Basics")
    .setMaster("local")

  val sc = new SparkContext(configuration)

  val vertexArray = Array(
    (1L, ("Alice", 28)),
    (2L, ("Bob", 27)),
    (3L, ("Charlie", 65)),
    (4L, ("David", 42)),
    (5L, ("Ed", 55)),
    (6L, ("Fran", 50))
  )

  val edgeArray = Array(
    Edge(2L, 1L, 7),
    Edge(2L, 4L, 2),
    Edge(3L, 2L, 4),
    Edge(3L, 6L, 3),
    Edge(4L, 1L, 1),
    Edge(5L, 2L, 2),
    Edge(5L, 3L, 8),
    Edge(5L, 6L, 3)
  )

  val vertexRDD: RDD[(Long, (String, Int))] = sc.parallelize(vertexArray)
  val edgeRDD: RDD[Edge[Int]] = sc.parallelize(edgeArray)

  val graph: Graph[(String, Int), Int] = Graph(vertexRDD, edgeRDD)

  def main(args: Array[String]): Unit = {
    graph.vertices.filter(v => v._2._2 > 30).collect.foreach(v => println(s"${v._2._1} is ${v._2._2}"))
  }

}
