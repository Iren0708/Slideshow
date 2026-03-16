module com.example.slideshow {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.example.slideshow to javafx.fxml;
    exports com.example.slideshow;

}