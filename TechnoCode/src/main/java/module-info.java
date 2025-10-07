module com.example.technocode {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.technocode to javafx.fxml;
    exports com.example.technocode;
    exports com.example.technocode.Controllers;
    opens com.example.technocode.Controllers to javafx.fxml;
}