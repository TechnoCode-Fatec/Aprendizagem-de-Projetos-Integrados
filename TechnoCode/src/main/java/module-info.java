module com.example.technocode {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.technocode to javafx.fxml;
    opens com.example.technocode.Controllers to javafx.fxml;
    opens com.example.technocode.Objetos to javafx.base; // Adicione esta linha

    exports com.example.technocode;
    exports com.example.technocode.Controllers;
    exports com.example.technocode.Objetos;
}
