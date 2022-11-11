package io.vacco.kimaris.facedet;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class Main extends Application {

  @Override
  public void start(Stage primaryStage) {
    // using your example image
    ImageView imageView = new ImageView("https://www.hyperledger.org/wp-content/uploads/2021/10/HL_HomepageIllustration_b-2048x563.png");

    Circle circle = new Circle(100, 100, 25, Color.FIREBRICK);
    circle.setOnMousePressed(
        e -> {
          // prevent pannable ScrollPane from changing cursor on drag-detected (implementation
          // detail)
          e.setDragDetect(false);
          Point2D offset =
              new Point2D(e.getX() - circle.getCenterX(), e.getY() - circle.getCenterY());
          circle.setUserData(offset);
          e.consume(); // prevents MouseEvent from reaching ScrollPane
        });
    circle.setOnMouseDragged(
        e -> {
          // prevent pannable ScrollPane from changing cursor on drag-detected (implementation
          // detail)
          e.setDragDetect(false);
          Point2D offset = (Point2D) circle.getUserData();
          circle.setCenterX(e.getX() - offset.getX());
          circle.setCenterY(e.getY() - offset.getY());
          e.consume(); // prevents MouseEvent from reaching ScrollPane
        });

    // the zoom-able content of the ScrollPane
    Group group = new Group(imageView, circle);

    // wrap Group in another Group since it's the former that's scaled and
    // Groups only take transformations of their **children** into account (not themselves)
    StackPane content = new StackPane(new Group(group));
    content.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    // due to later configuration, the StackPane will always cover the entire viewport
    content.setOnScroll(
        e -> {
          if (e.isShortcutDown() && e.getDeltaY() != 0) {
            if (e.getDeltaY() < 0) {
              group.setScaleX(Math.max(group.getScaleX() - 0.1, 0.5));
            } else {
              group.setScaleX(Math.min(group.getScaleX() + 0.1, 5.0));
            }
            group.setScaleY(group.getScaleX());
            e.consume(); // prevents ScrollEvent from reaching ScrollPane
          }
        });

    // use StackPane (or some other resizable node) as content since Group is not
    // resizable. Note StackPane will center content if smaller than viewport.
    ScrollPane scrollPane = new ScrollPane(content);
    scrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);
    scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
    scrollPane.setPannable(true);
    // ensure StackPane content always has at least the same dimensions as the viewport
    scrollPane.setFitToWidth(true);
    scrollPane.setFitToHeight(true);

    primaryStage.setScene(new Scene(scrollPane, 1000, 650));
    primaryStage.show();
  }
}
