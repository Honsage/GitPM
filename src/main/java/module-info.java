module ru.honsage.dev.gitpm {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires org.eclipse.jgit;
    requires org.slf4j;
    requires org.slf4j.simple;
    requires java.desktop;

    opens ru.honsage.dev.gitpm.presentation.controllers to javafx.fxml;
    exports ru.honsage.dev.gitpm;
}