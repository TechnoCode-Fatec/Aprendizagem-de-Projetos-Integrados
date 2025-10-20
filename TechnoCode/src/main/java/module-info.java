module com.example.technocode {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.base;
    requires javafx.graphics;

    opens com.example.technocode to javafx.fxml;
    opens com.example.technocode.Controllers to javafx.fxml;
    // Adicione esta linha

    exports com.example.technocode;
    exports com.example.technocode.Controllers;
    exports com.example.technocode.Objetos;
    opens com.example.technocode.Objetos to javafx.base, javafx.fxml;
}
