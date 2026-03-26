package com.seminairepoker.frontend.infrastructure.assets;

import com.seminairepoker.frontend.shared.cards.CardCodes;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AssetLoader {
    private static final String PRIMARY_ASSETS_ROOT = "/com/seminairepoker/frontend/assets/";
    private static final String SECONDARY_ASSETS_ROOT = "/assets/";
    private static final Logger LOGGER = Logger.getLogger(AssetLoader.class.getName());

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
        Image cardImage = loadCardImage(safeCardCode);

        if (cardImage != null) {
            ImageView imageView = new ImageView(cardImage);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(width);
            imageView.setFitHeight(height);
            applyCardCropAndClip(imageView, cardImage, width, height);
            return imageView;
        }

        return createFallback(
                width,
                height,
                safeCardCode.replace("_", " ").toUpperCase(Locale.ROOT),
                "card-fallback"
        );
    }

    private Image loadCardImage(String cardCode) {
        for (String fileName : resolveCardFileNames(cardCode)) {
            Image image = loadImage(fileName);
            if (image != null) {
                return image;
            }
        }
        return null;
    }

    private List<String> resolveCardFileNames(String cardCode) {
        if (CardCodes.HIDDEN.equals(cardCode)) {
            return List.of(cardCode + ".jpg", cardCode + ".png");
        }
        return List.of(cardCode + ".png", cardCode + ".jpg");
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
            LOGGER.log(Level.WARNING, "Unable to load asset: " + absolutePath, exception);
            return null;
        }
    }

    private String sanitizeCardCode(String cardCode) {
        return CardCodes.normalizeOrHidden(cardCode);
    }

    private void applyCardCropAndClip(ImageView imageView, Image image, double width, double height) {
        applyTrimmedViewport(imageView, image);

        Rectangle clip = new Rectangle(width, height);
        applyRoundedCorners(clip, width, height);
        imageView.setClip(clip);

        imageView.fitWidthProperty().addListener((observable, oldValue, newValue) -> {
            double clippedWidth = newValue == null ? width : newValue.doubleValue();
            clip.setWidth(clippedWidth);
            applyRoundedCorners(clip, clippedWidth, clip.getHeight());
        });
        imageView.fitHeightProperty().addListener((observable, oldValue, newValue) -> {
            double clippedHeight = newValue == null ? height : newValue.doubleValue();
            clip.setHeight(clippedHeight);
            applyRoundedCorners(clip, clip.getWidth(), clippedHeight);
        });
    }

    private void applyRoundedCorners(Rectangle clip, double width, double height) {
        double minSide = Math.max(1, Math.min(width, height));
        double arc = Math.max(8, minSide * 0.16);
        clip.setArcWidth(arc);
        clip.setArcHeight(arc);
    }

    private void applyTrimmedViewport(ImageView imageView, Image image) {
        PixelReader pixelReader = image.getPixelReader();
        if (pixelReader == null) {
            return;
        }

        int imageWidth = (int) Math.round(image.getWidth());
        int imageHeight = (int) Math.round(image.getHeight());
        if (imageWidth <= 0 || imageHeight <= 0) {
            return;
        }

        int minX = imageWidth;
        int minY = imageHeight;
        int maxX = -1;
        int maxY = -1;

        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {
                int alpha = (pixelReader.getArgb(x, y) >>> 24) & 0xFF;
                if (alpha == 0) {
                    continue;
                }
                minX = Math.min(minX, x);
                minY = Math.min(minY, y);
                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);
            }
        }

        if (maxX < minX || maxY < minY) {
            return;
        }

        int viewportWidth = (maxX - minX) + 1;
        int viewportHeight = (maxY - minY) + 1;
        if (viewportWidth == imageWidth && viewportHeight == imageHeight) {
            return;
        }

        imageView.setViewport(new Rectangle2D(minX, minY, viewportWidth, viewportHeight));
    }
}
