module common {
    requires java.sql;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    opens com.hotel.common.dto to com.fasterxml.jackson.databind;
    opens com.hotel.common.entities to com.fasterxml.jackson.databind;
    opens com.hotel.common.enums to com.fasterxml.jackson.databind;
    opens com.hotel.common.network to com.fasterxml.jackson.databind;

    exports com.hotel.common.dto;
    exports com.hotel.common.entities;
    exports com.hotel.common.enums;
    exports com.hotel.common.network;
}
