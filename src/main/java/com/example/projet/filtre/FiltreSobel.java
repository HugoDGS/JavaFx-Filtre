package com.example.projet.filtre;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class FiltreSobel implements ImageFilter{
    @Override
    public Image applyFilter(Image image) {
        PixelReader pixelReader = image.getPixelReader();
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage resultImage = new WritableImage(width, height);
        PixelWriter pixelWriter = resultImage.getPixelWriter();

        int[][] sobelX = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
        int[][] sobelY = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                double gradientX = 0, gradientY = 0;
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        Color color = pixelReader.getColor(x + i, y + j);
                        double intensity = color.getRed() * 0.21 + color.getGreen() * 0.72 + color.getBlue() * 0.07;
                        gradientX += intensity * sobelX[i + 1][j + 1];
                        gradientY += intensity * sobelY[i + 1][j + 1];
                    }
                }

                double magnitude = Math.sqrt(gradientX * gradientX + gradientY * gradientY);

                magnitude = Math.min(1, magnitude / (2 * Math.sqrt(2)));

                pixelWriter.setColor(x, y, Color.gray(magnitude));
            }
        }
        return resultImage;
    }
}
