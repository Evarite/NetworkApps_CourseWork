package com.hotel.client.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.Optional;

public class RatingDialog {

    private int selectedRating = 0;

    public Optional<Integer> show(int reservationId) {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Высяленне госця");
        dialog.setHeaderText(null);

        // Кнопкі
        ButtonType confirmType = new ButtonType("Выселіць", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelType = new ButtonType("Скасаваць", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmType, cancelType);

        Button confirmBtn = (Button) dialog.getDialogPane().lookupButton(confirmType);
        confirmBtn.setDisable(true); // пакуль не выбрана адзнака
        confirmBtn.getStyleClass().add("hotel-btn-primary");

        // Змест
        VBox content = new VBox(20);
        content.setPadding(new Insets(24, 28, 16, 28));
        content.setAlignment(Pos.CENTER_LEFT);

        Label infoLbl = new Label("Браніраванне #" + reservationId);
        infoLbl.getStyleClass().add("rating-info-label");

        Label titleLbl = new Label("Выстаўце адзнаку госцю:");
        titleLbl.getStyleClass().add("rating-title-label");

        // Зоркі
        HBox stars = new HBox(6);
        stars.setAlignment(Pos.CENTER_LEFT);
        Label[] starLabels = new Label[5];

        for (int i = 1; i <= 5; i++) {
            Label star = new Label("★");
            star.getStyleClass().addAll("star-label", "star-empty");
            final int rating = i;
            star.setOnMouseClicked(e -> {
                selectedRating = rating;
                updateStars(starLabels, rating);
                confirmBtn.setDisable(false);
            });
            star.setOnMouseEntered(e -> updateStars(starLabels, rating));
            star.setOnMouseExited(e -> updateStars(starLabels, selectedRating));
            starLabels[i - 1] = star;
            stars.getChildren().add(star);
        }

        Label ratingDesc = new Label("Абярыце адзнаку");
        ratingDesc.getStyleClass().add("rating-desc-label");

        // Абнаўляем апісанне пры выбары
        for (int i = 1; i <= 5; i++) {
            final int r = i;
            starLabels[i - 1].setOnMouseClicked(e -> {
                selectedRating = r;
                updateStars(starLabels, r);
                ratingDesc.setText(ratingToText(r));
                confirmBtn.setDisable(false);
            });
            starLabels[i - 1].setOnMouseEntered(e -> ratingDesc.setText(ratingToText(r)));
            starLabels[i - 1].setOnMouseExited(e -> ratingDesc.setText(
                    selectedRating > 0 ? ratingToText(selectedRating) : "Абярыце адзнаку"));
        }

        content.getChildren().addAll(infoLbl, titleLbl, stars, ratingDesc);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setMinWidth(360);

        dialog.setResultConverter(bt -> {
            if (bt == confirmType && selectedRating > 0) return selectedRating;
            return null;
        });

        return dialog.showAndWait();
    }

    private void updateStars(Label[] starLabels, int upTo) {
        for (int i = 0; i < 5; i++) {
            starLabels[i].getStyleClass().removeAll("star-filled", "star-empty");
            starLabels[i].getStyleClass().add(i < upTo ? "star-filled" : "star-empty");
        }
    }

    private String ratingToText(int r) {
        return switch (r) {
            case 1 -> "1 — Дрэнна";
            case 2 -> "2 — Ніжэй за сярэдняе";
            case 3 -> "3 — Нармальна";
            case 4 -> "4 — Добра";
            case 5 -> "5 — Выдатна";
            default -> "";
        };
    }
}
