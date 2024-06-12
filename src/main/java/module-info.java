module com.mycompany.petshop_db {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql;

    opens com.mycompany.petshop_db to javafx.fxml;
    exports com.mycompany.petshop_db;
    
    opens com.mycompany.petshop_db.controllers to javafx.fxml;
    opens com.mycompany.petshop_db.models to javafx.base;
}
