package GUI

import MovieDatabaseAccess.MovieCreator
import MovieDatabaseAcess.Main
import org.apache.hadoop.yarn.api.protocolrecords.GetQueueUserAclsInfoRequest
import event_filters.DraggablePanelsExample._


import scala.collection.mutable.ArrayBuffer
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control._
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{BorderPane, Pane, VBox, HBox}
import scalafx.scene.{Node, Scene}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle

class Graphics(){

        def main(args: Array[String]) {
                 val v:ViewTest = new ViewTest
        }
}


class ViewTest() extends JFXApp {


  var flag = true
  val bingImageAccess = new BingImageAccess

  var mov1Choices = ObservableBuffer("hej","hejja")
  var mov2Choices = ObservableBuffer("jarra","nejjra")

  var movRecs = ArrayBuffer.empty[String]
  var actRecs = ArrayBuffer.empty[String]
  var dirRecs = ArrayBuffer.empty[String]

  val mov = Main.getMov()
  val TextFieldOne = new TextField() {
    prefColumnCount = 10
    promptText = "Movie1"
  }

  val TextFieldTwo = new TextField() {
    prefColumnCount = 10
    promptText = "Movie2"
  }

  val combobox1 = new ComboBox[String]{
    maxWidth = 400
    minWidth = 300
    promptText = "Make a choice..."
    items = mov1Choices
  }

  val combobox2 = new ComboBox[String]{
    maxWidth = 400
    minWidth = 300
    promptText = "Make a choice..."
    items = mov2Choices
  }




  val actors = new Label("\n\n\n")
  val directors = new Label("\n\n\n")
  val image1Text = new Label("\n\n\n")
  val image2Text = new Label("\n\n\n")
  val image3Text = new Label("\n\n\n")
  val image4Text = new Label("\n\n\n")

  val image1 = new ImageView(new Image("http://www.clker.com/cliparts/8/D/k/N/g/r/white-dollar-sign-2-md.png")) {
    // One can resize image without preserving ratio between height and width
    fitHeight = 400
    preserveRatio = true
    // The usage of the better filter
    smooth = true
  }
  val image2 = new ImageView(new Image("http://www.clker.com/cliparts/8/D/k/N/g/r/white-dollar-sign-2-md.png")) {
    // One can resize image without preserving ratio between height and width
    fitHeight = 400
    preserveRatio = true
    // The usage of the better filter
    smooth = true
  }
  val image3 = new ImageView(new Image("http://www.clker.com/cliparts/8/D/k/N/g/r/white-dollar-sign-2-md.png")) {
    // One can resize image without preserving ratio between height and width
    fitHeight = 400
    preserveRatio = true
    // The usage of the better filter
    smooth = true
  }
  val image4 = new ImageView(new Image("http://www.clker.com/cliparts/8/D/k/N/g/r/white-dollar-sign-2-md.png")) {
    // One can resize image without preserving ratio between height and width
    fitHeight = 400
    preserveRatio = true
    // The usage of the better filter
    smooth = true
  }
  val imageActor = new ImageView(new Image("http://www.clker.com/cliparts/8/D/k/N/g/r/white-dollar-sign-2-md.png")) {
    // One can resize image without preserving ratio between height and width
    fitHeight = 150
    preserveRatio = true
    // The usage of the better filter
    smooth = true
  }
  val imageDir = new ImageView(new Image("http://www.clker.com/cliparts/8/D/k/N/g/r/white-dollar-sign-2-md.png")) {
    // One can resize image without preserving ratio between height and width
    fitHeight = 150
    preserveRatio = true
    // The usage of the better filter
    smooth = true
  }

  val images =Seq()
  private val borderStyle = "" +
    "-fx-background-color: white;" +
    "-fx-border-color: black;" +
    "-fx-border-width: 1;" +
    "-fx-border-radius: 6;" +
    "-fx-padding: 6;"

  stage = new JFXApp.PrimaryStage() {

    val panelsPane = new Pane() {
      val loginPanel = createLoginPanel()

      loginPanel.relocate(0, 0)

      children = Seq(loginPanel)
      alignmentInParent = Pos.TopLeft
    }
    title = "Movie Recommendation"
    scene = new Scene(1300, 700) {
      root = new BorderPane() {
        center = panelsPane
      }
    }
  }







  private def updateImages(): Unit ={
    val size = movRecs.size
    var actorsString: String = "Recommended Actors: \n"
    for(a<-actRecs){ actorsString += a+ "\n" }
    var dirString: String = "Recommended Directors: \n"
    for(a<-dirRecs){ dirString += a+ "\n" }

    actors.setText(actorsString)
    directors.setText(dirString)


    if (size>=1){
      image1.setImage(new Image(url = mov.titleMovie(movRecs(0)).Poster))
      image1Text.setText(mov.titleMovie(movRecs(0)).Title +"\n"
         +"Imdb Score: "+ mov.titleMovie(movRecs(0)).imdbRating + "\n" + "Released: "+mov.titleMovie(movRecs(0)).Year )}
    else{
      image1.setImage(new Image(url = "http://www.clker.com/cliparts/8/D/k/N/g/r/white-dollar-sign-2-md.png"))
      image1Text.setText("")
    }

    if (size>=2){
      image2.setImage(new Image(url = mov.titleMovie(movRecs(1)).Poster))
      image2Text.setText(mov.titleMovie(movRecs(1)).Title +"\n"
        +"Imdb Score: "+ mov.titleMovie(movRecs(1)).imdbRating + "\n" + "Released: "+mov.titleMovie(movRecs(1)).Year )}
    else{
      image2.setImage(new Image(url = "http://www.clker.com/cliparts/8/D/k/N/g/r/white-dollar-sign-2-md.png"))
      image2Text.setText("")}
    if (size>=3){
      image3.setImage(new Image(url = mov.titleMovie(movRecs(2)).Poster))
      image3Text.setText(mov.titleMovie(movRecs(2)).Title +"\n"
        +"Imdb Score: "+ mov.titleMovie(movRecs(2)).imdbRating + "\n" + "Released: "+mov.titleMovie(movRecs(2)).Year )}
    else{
      image3.setImage(new Image(url = "http://www.clker.com/cliparts/8/D/k/N/g/r/white-dollar-sign-2-md.png"))
      image3Text.setText("")}
    if (size>=4){
      image4.setImage(new Image(url = mov.titleMovie(movRecs(3)).Poster))
      image4Text.setText(mov.titleMovie(movRecs(3)).Title +"\n"
        +"Imdb Score: "+ mov.titleMovie(movRecs(3)).imdbRating + "\n" + "Released: "+mov.titleMovie(movRecs(3)).Year )}
    else{
      image4.setImage(new Image(url = "http://www.clker.com/cliparts/8/D/k/N/g/r/white-dollar-sign-2-md.png"))
      image4Text.setText("")
    }

    val actorIm = bingImageAccess.getImage(mov.ViewTest.actRecs(0))
    val dirI = bingImageAccess.getImage(mov.ViewTest.dirRecs(0))
    imageActor.setImage(new Image(url = actorIm))
    imageDir.setImage(new Image(url = dirI))





  }
  private def createLoginPanel(): Node = {
    val toggleGroup1 = new ToggleGroup()


    new VBox(6) {
      children = Seq(
        new HBox(2){
          children = Seq(

            new VBox(2){
            children= Seq(image1,image1Text)},

            new VBox(2){
              children= Seq(image2,image2Text)},

            new VBox(2){
                children= Seq(image3,image3Text)},

            new VBox(2){
                  children= Seq(image4,image4Text)}
          )},

        new HBox(2){
          children = Seq(

/*
          new VBox(2) {
            children = Seq(
              new RadioButton("IMDB>5") {
                toggleGroup = toggleGroup1
                userData = "5"
                selected = true
              },
              new RadioButton("IMDB>6") {
                toggleGroup = toggleGroup1
                userData = "6"
                selected = false
              },
              new RadioButton("IMDB>7") {
                toggleGroup = toggleGroup1
                userData = "7"
                selected = false
              }
            )
          },*/
        new VBox(2) {
          children = Seq(
            new HBox(2){
              children = Seq(TextFieldOne,
              new Button("Search"){
                onAction = handle{ mov.findMovieIdX(TextFieldOne.text(),1)}
              }, combobox1
              )}, new HBox(2){
              children = Seq(TextFieldTwo,
                new Button("Search"){
                  onAction = handle{ mov.findMovieIdX(TextFieldTwo.text(),2)}
                },combobox2
              )})
        },
        new HBox(2){
          children = Seq(
          new Button("Recommend Me!"){
            onAction =handle{
              new Thread(new Runnable {
                def run() {
                  val ret = Main.graphRun(combobox1.getValue,combobox2.getValue)
                  mov.ViewTest.movRecs = ret._1
                  mov.ViewTest.actRecs = ret._2
                  mov.ViewTest.dirRecs = ret._3
                  mov.ViewTest.flag = false
                }
              }).start()

              while(mov.ViewTest.flag){

                Thread.sleep(10)
              }
              updateImages()
              mov.ViewTest.flag = true

              } //
          }
          )


            }
          )
        },
    new HBox(100){
      children = Seq(
        new VBox(10){
          children=Seq(imageActor,actors)},
        new VBox(10){
          children = Seq(imageDir,directors)})}
      )

      alignment = Pos.BottomLeft
      style = borderStyle


  }}


        }