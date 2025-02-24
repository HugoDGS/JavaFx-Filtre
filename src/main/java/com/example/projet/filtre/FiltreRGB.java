package com.example.projet.filtre;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class FiltreRGB implements ImageFilter{
    @Override
    public Image applyFilter(Image image) {

        PixelReader pixelReader = image.getPixelReader();
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage resultImage = new WritableImage(width, height);
        PixelWriter pixelWriter = resultImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);
                Color newColor = Color.color(color.getBlue(), color.getGreen(), color.getRed(), color.getOpacity());
                pixelWriter.setColor(x, y, newColor);
            }
        }
        return resultImage;
    }
}
