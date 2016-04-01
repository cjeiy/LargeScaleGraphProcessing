import event_filters.DraggablePanelsExample._

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

class KL(){

        def main(args: Array[String]) {
                 ViewTest
        }
}

object ViewTest extends JFXApp {

  val testImage = new ImageView(new Image("http://g-ecx.images-amazon.com/images/G/01/img15/pet-products/small-tiles/23695_pets_vertical_store_dogs_small_tile_8._CB312176604_.jpg")) {
    // One can resize image without preserving ratio between height and width
    fitHeight = 140
    preserveRatio = true
    // The usage of the better filter
    smooth = true
  }


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
    title = "Draggable Panels Example"
    scene = new Scene(400, 300) {
      root = new BorderPane() {
        center = panelsPane
      }
    }
  }





  private def doSomething(a:String): Unit ={

    testImage.setImage(new Image("https://www.petfinder.com/wp-content/uploads/2012/11/140272627-grooming-needs-senior-cat-632x475.jpg"))
    println("GoGo!" + a)
  }
  private def createLoginPanel(): Node = {
    val toggleGroup1 = new ToggleGroup()

    val textField = new TextField() {
      prefColumnCount = 10
      promptText = "Movie1"
    }

    val passwordField = new TextField()() {
      prefColumnCount = 10
      promptText = "Movie2"
    }



    new VBox(6) {
      children = Seq(testImage,

        new HBox(2){
          children = Seq(


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
          },
        new VBox(2) {
          children = Seq(textField, passwordField)
        },
        new HBox(2){
          children = Seq(
          new Button("Recommend Me!"){
            onAction =handle{ doSomething(toggleGroup1.getSelectedToggle.getUserData.toString)}
          }
          )
        }
          )
        }
      )

      alignment = Pos.BottomLeft
      style = borderStyle


  }}

        }