module ru.honsage.dev.gitpm {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens ru.honsage.dev.gitpm.presentation.controllers to javafx.fxml;
    exports ru.honsage.dev.gitpm;
}