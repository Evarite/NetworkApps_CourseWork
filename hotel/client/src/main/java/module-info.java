module com.hotel.client {
    requires javafx.controls;
    requires javafx.base;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires java.sql;
    requires common;
    requires log4j;

    opens com.hotel.client to javafx.graphics;
    opens com.hotel.client.views to javafx.graphics;

    exports com.hotel.client;
}
