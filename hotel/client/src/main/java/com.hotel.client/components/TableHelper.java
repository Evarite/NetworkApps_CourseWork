package com.hotel.client.components;

import com.hotel.common.entities.Reservation;
import com.hotel.common.entities.Room;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;

import java.time.format.DateTimeFormatter;

/** Фабрыка табліц для даных гатэля. */
public class TableHelper {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    // ── Rooms table ──────────────────────────────────────────────────────────

    public static TableView<Room> roomTable() {
        TableView<Room> table = new TableView<>();
        table.getStyleClass().add("hotel-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("Нумароў не знойдзена"));

        table.getColumns().addAll(
                strCol("Нумар", 80,  r -> String.valueOf(r.getNumber())),
                strCol("Паверх", 80,  r -> String.valueOf(r.getFloor())),
                strCol("Тып",   140, r -> roomTypeStr(r.getType())),
                strCol("Мяшч.", 110, r -> capacityStr(r.getCapacity())),
                strCol("Статус", 120, r -> roomStatusStr(r.getStatus())),
                strCol("Цана/ноч", 110, r -> String.format("%.2f BYN", r.getPrice())),
                strCol("Апісанне", 200, Room::getDescription)
        );
        return table;
    }

    // ── Reservation table ────────────────────────────────────────────────────

    public static TableView<Reservation> reservationTable() {
        TableView<Reservation> table = new TableView<>();
        table.getStyleClass().add("hotel-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("Браніраванняў не знойдзена"));

        table.getColumns().addAll(
                strCol("ID",         60,  r -> String.valueOf(r.getId())),
                strCol("Госць ID",  90,  r -> String.valueOf(r.getGuestId())),
                strCol("Нумар",     80,  r -> String.valueOf(r.getRoomNumber())),
                strCol("Дата заезду", 120, r -> r.getReservationDate().format(FMT)),
                strCol("Начоў",     70,  r -> String.valueOf(r.getDuration())),
                strCol("Статус",   130, r -> reservationStatusStr(r.getStatus()))
        );
        return table;
    }

    // ── Generic col builder ──────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private static <T> TableColumn<T, String> strCol(String name, double minW,
                                                      java.util.function.Function<T, String> fn) {
        TableColumn<T, String> col = new TableColumn<>(name);
        col.setMinWidth(minW);
        col.setCellValueFactory(cd -> {
            String val = cd.getValue() == null ? "" : fn.apply(cd.getValue());
            return new SimpleStringProperty(val == null ? "" : val);
        });
        return col;
    }

    // ── String conversions ───────────────────────────────────────────────────

    public static String roomTypeStr(Room.Type t) {
        return switch (t) {
            case STANDARD    -> "Стандарт";
            case SUPERIOR    -> "Супэрыёр";
            case JUNIOR_SUITE -> "Джуніёр сюіт";
            case SUITE       -> "Сюіт";
            case APARTMENTS  -> "Апартаменты";
            case PRESIDENT   -> "Прэзідэнт";
        };
    }

    public static String capacityStr(Room.Capacity c) {
        return switch (c) {
            case SINGLE -> "1 ч.";
            case DOUBLE -> "2 ч.";
            case TWIN   -> "2 (2 ложкі)";
            case TRIPLE -> "3 ч.";
            case FAMILY -> "Сямейны";
        };
    }

    public static String roomStatusStr(Room.Status s) {
        return switch (s) {
            case AVAILABLE   -> "Свабодны";
            case OCCUPIED    -> "Занятны";
            case MAINTENANCE -> "Тэхаб.";
        };
    }

    public static String reservationStatusStr(Reservation.Status s) {
        return switch (s) {
            case PENDING    -> "Чакае";
            case APPROVED   -> "Зацверджана";
            case CANCELLED  -> "Скасавана";
            case CHECKED_OUT -> "Выселены";
        };
    }
}
