package com.example.projet.filtre;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class SecureRandomImageFilter {

    public static Image applyFilter(Image originalImage, String password) {
        byte[] passwordBytes = password.getBytes();
        byte[] hashedBytes = hashPassword(passwordBytes);
        SecureRandom random = new SecureRandom(hashedBytes);

        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();

        WritableImage filteredImage = new WritableImage(width, height);
        PixelWriter pixelWriter = filteredImage.getPixelWriter();
        PixelReader pixelReader = originalImage.getPixelReader();

        int[] permutation = generatePermutation(width * height, random);
        int index = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int newX = permutation[index] % width;
                int newY = permutation[index] / width;
                Color color = pixelReader.getColor(newX, newY);
                pixelWriter.setColor(x, y, color);
                index++;
            }
        }

        return filteredImage;
    }

    private static byte[] hashPassword(byte[] passwordBytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(passwordBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static int[] generatePermutation(int size, SecureRandom random) {
        int[] permutation = new int[size];
        for (int i = 0; i < size; i++) {
            permutation[i] = i;
        }
        for (int i = size - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int temp = permutation[index];
            permutation[index] = permutation[i];
            permutation[i] = temp;
        }
        return permutation;
    }
}
