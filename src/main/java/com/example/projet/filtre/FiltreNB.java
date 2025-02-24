package com.example.projet.filtre;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class FiltreNB implements ImageFilter {
    @Override
    public Image applyFilter(Image image) {
        PixelReader pixelReader = image.getPixelReader();
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage resultImage = new WritableImage(width, height);
        PixelWriter pixelWriter = resultImage.getPixelWriter();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Récupération de la couleur du pixel d'origine
                Color color = pixelReader.getColor(x, y);
                // Calcul de la luminosité moyenne du pixel
                double luminosity = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
                //nouvelle couleur utilisant la luminosité comme valeur pour chaque composante de couleur
                Color newColor = Color.color(luminosity, luminosity, luminosity, color.getOpacity());
                pixelWriter.setColor(x, y, newColor);
            }
        }
        return resultImage;
    }
}
