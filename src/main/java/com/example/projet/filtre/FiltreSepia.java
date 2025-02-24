package com.example.projet.filtre;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class FiltreSepia implements ImageFilter {
    @Override
    public Image applyFilter(Image image) {
        PixelReader pixelReader = image.getPixelReader();
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage resultImage = new WritableImage(width, height);
        PixelWriter pixelWriter = resultImage.getPixelWriter();

        final double rCoefficient = 0.393;
        final double gCoefficient = 0.769;
        final double bCoefficient = 0.189;

        final double rSepia = 0.272;
        final double gSepia = 0.534;
        final double bSepia = 0.131;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);

                double r = color.getRed();
                double g = color.getGreen();
                double b = color.getBlue();

                double newR = Math.min(1, (r * rCoefficient) + (g * gCoefficient) + (b * bCoefficient));
                double newG = Math.min(1, (r * rSepia) + (g * gCoefficient) + (b * bSepia));
                double newB = Math.min(1, (r * rSepia) + (g * gSepia) + (b * bCoefficient));

                pixelWriter.setColor(x, y, new Color(newR, newG, newB, color.getOpacity()));
            }
        }
        return resultImage;
    }
}
