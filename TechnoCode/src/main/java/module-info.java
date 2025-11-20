module com.example.technocode {
    // --- Módulos JavaFX necessários ---
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires javafx.web;
    requires java.prefs;

    // --- Outras dependências do Java ---
    requires java.sql;

    // --- Abertura de pacotes (para reflexão via FXML) ---
    opens com.example.technocode to javafx.graphics, javafx.fxml;
    opens com.example.technocode.Controllers to javafx.fxml;
    opens com.example.technocode.Controllers.Aluno to javafx.fxml;
    opens com.example.technocode.Controllers.Orientador to javafx.fxml;
    opens com.example.technocode.model to javafx.base;
    opens com.example.technocode.Controllers.ProfessorTG to javafx.fxml;
    opens com.example.technocode.model.dao to javafx.base;


    // --- Exportação de pacotes (visíveis a outros módulos) ---
    exports com.example.technocode;
    exports com.example.technocode.Controllers;
    exports com.example.technocode.Controllers.Aluno;
    exports com.example.technocode.Controllers.Orientador;
    exports com.example.technocode.model;
}