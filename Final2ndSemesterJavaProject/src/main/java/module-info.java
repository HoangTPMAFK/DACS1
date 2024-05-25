module com.entity.demo1 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires java.sql;
    requires mysql.connector.java;

    opens com.entity.demo to javafx.fxml;
    exports com.entity.demo;
}