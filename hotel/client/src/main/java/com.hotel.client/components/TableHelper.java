package com.hotel.client.components;

import com.hotel.common.entities.Employee;
import com.hotel.common.entities.Guest;
import com.hotel.common.entities.Reservation;
import com.hotel.common.entities.Room;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.*;

import java.time.format.DateTimeFormatter;
import java.util.function.Function;

public class TableHelper {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static TableView<Room> roomTable() {
        TableView<Room> table = styled(new TableView<>(), "Нумароў не знойдзена");

        table.getColumns().addAll(
                strCol("Нумар", 80, r -> String.valueOf(r.getNumber())),
                strCol("Паверх", 70, r -> String.valueOf(r.getFloor())),
                strCol("Тып", 140, r -> roomTypeStr(r.getType())),
                strCol("Мяшч.", 110, r -> capacityStr(r.getCapacity())),
                strCol("Статус", 110, r -> roomStatusStr(r.getStatus())),
                strCol("Кошт/ноч", 110, r -> String.format("%.2f BYN", r.getPrice())),
                strCol("Апісанне", 200, Room::getDescription)
        );
        return table;
    }

    public static TableView<Reservation> reservationTable() {
        TableView<Reservation> table = styled(new TableView<>(), "Браніраванняў не знойдзена");

        table.getColumns().addAll(
                strCol("ID", 60, r -> String.valueOf(r.getId())),
                strCol("Госць ID", 90, r -> String.valueOf(r.getGuestId())),
                strCol("Нумар", 80, r -> String.valueOf(r.getRoomNumber())),
                strCol("Дата заезду", 120, r -> r.getReservationDate().format(FMT)),
                strCol("Начоў", 70, r -> String.valueOf(r.getDuration())),
                strCol("Статус", 130, r -> reservationStatusStr(r.getStatus()))
        );
        return table;
    }

    public static TableView<Guest> guestTable() {
        TableView<Guest> table = styled(new TableView<>(), "Гасцей не знойдзена");

        table.getColumns().addAll(
                strCol("ID акаўнта", 100, g -> String.valueOf(g.getAccountId())),
                strCol("Сярэдні рэйт.", 140, g -> ratingStars(g.getAverageRating())),
                strCol("Балы", 80, g -> String.format("%.1f", g.getRating())),
                strCol("Адзнак", 80, g -> String.valueOf(g.getRatingsAmount())),
                strCol("Браніраванняў", 120, g -> String.valueOf(g.getReservationsAmount()))
        );
        return table;
    }

    public static TableView<Employee> employeeTable() {
        TableView<Employee> table = styled(new TableView<>(), "Супрацоўнікаў не знойдзена");

        table.getColumns().addAll(
                strCol("ID акаўнта", 100, e -> String.valueOf(e.getAccountId())),
                strCol("Пасада", 160, e -> positionStr(e.getPosition())),
                strCol("Заробак", 120, e -> String.format("%.2f BYN", e.getSalary())),
                strCol("Дата прыёму", 130, e -> e.getHireDate() != null
                        ? e.getHireDate().format(FMT) : "—")
        );
        return table;
    }

    private static <T> TableView<T> styled(TableView<T> table, String emptyText) {
        table.getStyleClass().add("hotel-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label(emptyText));
        return table;
    }

    private static <T> TableColumn<T, String> strCol(String name, double minW, Function<T, String> fn) {
        TableColumn<T, String> col = new TableColumn<>(name);
        col.setMinWidth(minW);

        col.setCellValueFactory(cd -> {
            String val = cd.getValue() == null ? "" : fn.apply(cd.getValue());
            return new SimpleStringProperty(val == null ? "" : val);
        });

        // Цэнтраванне тэксту ў клетцы
        col.setCellFactory(tc -> {
            TableCell<T, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item);
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });

        return col;
    }

    public static String roomTypeStr(Room.Type t) {
        return switch (t) {
            case STANDARD -> "Стандарт";
            case SUPERIOR -> "Палепшаны";
            case JUNIOR_SUITE -> "Паўлюкс";
            case SUITE -> "Люкс";
            case APARTMENTS -> "Апартаменты";
            case PRESIDENT -> "Прэзыденцкі люкс";
        };
    }

    public static String capacityStr(Room.Capacity c) {
        return switch (c) {
            case SINGLE -> "1 чал.";
            case DOUBLE -> "2 чал.";
            case TWIN -> "2 (2 ложкі)";
            case TRIPLE -> "3 чал.";
            case FAMILY -> "Сямейны";
        };
    }

    public static String roomStatusStr(Room.Status s) {
        return switch (s) {
            case AVAILABLE -> "Свабодны";
            case OCCUPIED -> "Занятны";
            case MAINTENANCE -> "Тэхабсл.";
        };
    }

    public static String reservationStatusStr(Reservation.Status s) {
        return switch (s) {
            case PENDING -> "Чакае";
            case APPROVED -> "Зацверджана";
            case CANCELLED -> "Скасавана";
            case CHECKED_OUT -> "Выселены";
        };
    }

    public static String positionStr(Employee.Position p) {
        if (p == null) return "—";
        return switch (p) {
            case RECEPTIONIST -> "Парцье";
            case MANAGER -> "Мэнэджар";
            case ADMINISTRATOR -> "Адміністратар";
        };
    }

    public static String ratingStars(float avg) {
        int full = (int) avg;
        int empty = 5 - full;
        return "★".repeat(full) + "☆".repeat(empty) + String.format("  %.1f", avg);
    }
}

