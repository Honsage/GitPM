module ru.honsage.dev.gitpm {
    requires javafx.controls;
    requires javafx.fxml;


    opens ru.honsage.dev.gitpm to javafx.fxml;
    exports ru.honsage.dev.gitpm;
}