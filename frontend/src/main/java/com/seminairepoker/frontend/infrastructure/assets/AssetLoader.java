package com.seminairepoker.frontend.infrastructure.assets;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import java.io.InputStream;
import java.util.Locale;

public class AssetLoader {
    private static final String PRIMARY_ASSETS_ROOT = "/com/seminairepoker/frontend/assets/";
    private static final String SECONDARY_ASSETS_ROOT = "/assets/";

    public Node loadTable(double width, double height) {
        Image tableImage = loadImage("table.png");
        if (tableImage == null) {
            tableImage = loadImage("Table.png");
        }
        if (tableImage != null) {
            ImageView imageView = new ImageView(tableImage);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(width);
            imageView.setFitHeight(height);
            return imageView;
        }
        return createFallback(width, height, "TABLE", "table-fallback");
    }

    public Node loadCard(String cardCode, double width, double height) {
        String safeCardCode = sanitizeCardCode(cardCode);
        return loadImageWithFallback(
                safeCardCode + ".png",
                width,
                height,
                safeCardCode.replace("_", " ").toUpperCase(Locale.ROOT),
                "card-fallback"
        );
    }

    private Node loadImageWithFallback(
            String fileName,
            double width,
            double height,
            String fallbackText,
            String fallbackClass
    ) {
        Image image = loadImage(fileName);
        if (image != null) {
            ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(width);
            imageView.setFitHeight(height);
            return imageView;
        }

        return createFallback(width, height, fallbackText, fallbackClass);
    }

    private Node createFallback(double width, double height, String fallbackText, String fallbackClass) {
        Rectangle rectangle = new Rectangle(width, height);
        rectangle.getStyleClass().add(fallbackClass);

        Label label = new Label(fallbackText);
        label.getStyleClass().add("fallback-label");

        StackPane fallback = new StackPane(rectangle, label);
        fallback.setAlignment(Pos.CENTER);
        return fallback;
    }

    private Image loadImage(String fileName) {
        Image image = loadFromRoot(fileName, PRIMARY_ASSETS_ROOT);
        if (image != null) {
            return image;
        }
        return loadFromRoot(fileName, SECONDARY_ASSETS_ROOT);
    }

    private Image loadFromRoot(String fileName, String root) {
        String absolutePath = root + fileName;
        try (InputStream inputStream = AssetLoader.class.getResourceAsStream(absolutePath)) {
            if (inputStream == null) {
                return null;
            }
            return new Image(inputStream);
        } catch (Exception exception) {
            return null;
        }
    }

    private String sanitizeCardCode(String cardCode) {
        String defaultCard = "back";
        if (cardCode == null || cardCode.isBlank()) {
            return defaultCard;
        }
        return cardCode.trim().toLowerCase(Locale.ROOT).replace(' ', '_');
    }
}
