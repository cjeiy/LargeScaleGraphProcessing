package MovieDatabaseAcess

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