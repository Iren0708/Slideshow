module com.example.slideshow {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires metadata.extractor;
    opens com.example.slideshow to javafx.fxml;
    exports com.example.slideshow;

}