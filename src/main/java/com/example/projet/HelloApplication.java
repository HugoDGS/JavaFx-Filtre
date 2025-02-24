package com.example.projet;

import com.example.projet.filtre.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.*;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.layout.HBox;
import javafx.scene.control.TextField;
import java.util.ArrayList;
import com.google.gson.*;
import javafx.scene.control.ButtonType;


public class HelloApplication extends Application {
    private Image originalImage;
    private ImageView imageView;
    private Button rotateButton;
    private Button flipButton;
    private Button rgbExchangeButton;
    private Button blackAndWhiteButton;
    private Button sepiaButton;
    private Button sobelButton;
    private Button resetButton;
    private TextField tagsField;
    private Button submitButton;
    private ArrayList<String> tagsList = new ArrayList<>();
    private ArrayList<Transformation> transformationsList = new ArrayList<>();
    private String fileName;
    private Button saveButton;
    private Button passwordButton;
    private Gson gson = new Gson();


    @Override
    public void start(Stage stage) throws IOException {
        File selectedFile = null;
        tagsList.clear();
        transformationsList.clear();
        while (selectedFile == null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            File defaultDirectory = new File("src/main/resources/images");
            fileChooser.setInitialDirectory(defaultDirectory);
            fileChooser.getExtensionFilters().addAll(
                    new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
            selectedFile = fileChooser.showOpenDialog(stage);
            fileName = selectedFile.getName();

        }

        imageView = new ImageView();
        imageView.setFitWidth(650);
        imageView.setFitHeight(450);

        imageView.setPreserveRatio(true);

        rotateButton = new Button("Rotate");
        rotateButton.setOnAction(e -> rotateImage());

        flipButton = new Button("Flip");
        flipButton.setOnAction(e -> flipImage());

        rgbExchangeButton = new Button("RGB Exchange");
        rgbExchangeButton.setOnAction(e -> applyFilter(new FiltreRGB()));

        blackAndWhiteButton = new Button("Black & White");
        blackAndWhiteButton.setOnAction(e -> applyFilter(new FiltreNB()));

        sepiaButton = new Button("Sepia");
        sepiaButton.setOnAction(e -> applyFilter(new FiltreSepia()));

        sobelButton = new Button("Sobel");
        sobelButton.setOnAction(e -> applyFilter(new FiltreSobel()));

        resetButton = new Button("Recommencer");
        resetButton.setOnAction(e -> resetImage());

        tagsField = new TextField();
        submitButton = new Button("Ajouter un tag");
        submitButton.setOnAction(e -> addWordToList());

        saveButton = new Button("Save");
        saveButton.setOnAction(e -> saveImage());

        Button passwordButton = new Button("Entrer le mot de passe");
        passwordButton.setOnAction(e -> showDialog());

        HBox imageButtonsBox = new HBox(10);
        imageButtonsBox.getChildren().addAll(rotateButton, flipButton, rgbExchangeButton, blackAndWhiteButton, sepiaButton, sobelButton, passwordButton ,resetButton);

        HBox tagsBox = new HBox(10);
        tagsBox.getChildren().addAll(tagsField, submitButton);

        HBox saveBox = new HBox(10);
        saveBox.getChildren().addAll(saveButton);

        VBox controlBox = new VBox(20);
        controlBox.getChildren().addAll(imageButtonsBox, tagsBox, saveBox);

        StackPane root = new StackPane();
        root.getChildren().addAll(imageView, controlBox);

        Scene scene = new Scene(root, 1200, 600);

        stage.setScene(scene);
        stage.setTitle("Visionneuse d'images");
        stage.show();

        if (selectedFile != null) {

            originalImage = new Image(selectedFile.toURI().toString());
            File f = new File("metadata.json");
            if(f.exists()){
                ArrayList<String> transformations = loadTransformationsForImage(fileName);
                for (String transformation : transformations) {
                    switch (transformation) {
                        case "rotate":
                            rotateImage();
                            break;
                        case "flip":
                            flipImage();
                            break;
                        case "rgb":
                            FiltreRGB rgb = new FiltreRGB();
                            originalImage = rgb.applyFilter(originalImage);
                            break;
                        case "blackandwhite":
                            FiltreNB nb = new FiltreNB();
                            originalImage = nb.applyFilter(originalImage);
                            break;
                        case "sepia":
                            FiltreSepia sepia = new FiltreSepia();
                            originalImage = sepia.applyFilter(originalImage);
                            break;
                        case "sobel":
                            FiltreSobel sobel = new FiltreSobel();
                            originalImage = sobel.applyFilter(originalImage);
                            break;
                        default:
                    }
                }
            }
            tagsList.clear();
            transformationsList.clear();
            imageView.setImage(originalImage);
        }
    }

    //méthode pour faire pivoter l'image de 90 degrés
    private void rotateImage() {
        imageView.setRotate(imageView.getRotate() + 90);
        double newWidth = imageView.getFitHeight();
        double newHeight = imageView.getFitWidth();
        imageView.setFitWidth(newWidth);
        imageView.setFitHeight(newHeight);
        transformationsList.add(new Transformation("rotate"));
    }

    //méthode pour retourner l'image horizontalement
    private void flipImage() {
        imageView.setScaleX(imageView.getScaleX() * -1);
        transformationsList.add(new Transformation("flip"));
    }

    //méthode pour appliquer un filtre à l'image
    private void applyFilter(ImageFilter filter) {
        Image currentImage = imageView.getImage();
        Image filteredImage = filter.applyFilter(currentImage);
        imageView.setImage(filteredImage);
        // Ajoute le type de filtre appliqué à la liste des transformations
        if (filter instanceof FiltreRGB) {
            transformationsList.add(new Transformation("rgb"));
        } else if (filter instanceof FiltreNB) {
            transformationsList.add(new Transformation("blackandwhite"));
        } else if (filter instanceof FiltreSepia) {
            transformationsList.add(new Transformation("sepia"));
        } else if (filter instanceof FiltreSobel) {
            transformationsList.add(new Transformation("sobel"));
        }
    }

    //méthode pour enregistrer les métadonnées de l'image dans un fichier JSON
    private void saveImage() {
        Metadata newMetadata = new Metadata(fileName, new ArrayList<>(tagsList), new ArrayList<>(transformationsList)); // Crée un nouvel objet Metadata avec les métadonnées de l'image
        File jsonFile = new File("metadata.json"); // Crée un nouveau fichier JSON
        boolean fileExists = jsonFile.exists();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("metadata.json", true))) {
            // Écriture dans le fichier JSON
            if (!fileExists) {
                writer.write("[");
            } else {
                BufferedReader reader = new BufferedReader(new FileReader("metadata.json"));
                if (reader.readLine() != null) {
                    writer.write(",");
                }
                reader.close();
            }
            writer.write(gson.toJson(newMetadata)); // Convertit les métadonnées en JSON et les écrit dans le fichier
            RandomAccessFile file = new RandomAccessFile("metadata.json", "rw");
            long length = file.length();
            if (length > 0) {
                file.setLength(length - 1);
            }
            file.close();
            writer.write("]");
        } catch (IOException e) {
            e.printStackTrace();
        }
        tagsList.clear();
        transformationsList.clear();
    }

    //méthode pour réinitialiser l'image à sa version originale
    private void resetImage() {
        if (originalImage != null) {
            imageView.setImage(originalImage);
            tagsList.clear();
            transformationsList.clear();
        }
    }

    //méthode pour ajouter tag à la liste
    private void addWordToList() {
        String word = tagsField.getText();
        if (!word.isEmpty()) {
            tagsList.add(word);
            tagsField.clear();
        }
    }

    public static void main(String[] args) {
        launch();
    }

    // charge les transformations pour une image à partir du fichier JSON
    private ArrayList<String> loadTransformationsForImage(String imageName) {
        ArrayList<String> transformations = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("metadata.json"))) {
            String line;
            StringBuilder jsonContent = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            JsonArray jsonArray = new JsonParser().parse(jsonContent.toString()).getAsJsonArray(); // Convertit le contenu JSON en tableau JSON
            // Parcourt le tableau JSON pour trouver les métadonnées de l'image spécifique
            for (JsonElement element : jsonArray) {
                JsonObject metadataObject = element.getAsJsonObject(); // Récupère l'objet JSON pour chaque élément du tableau
                if (metadataObject.get("imageName").getAsString().equals(imageName)) {
                    JsonArray transformationsArray = metadataObject.get("transformations").getAsJsonArray(); // Récupère le tableau des transformations pour cette image
                    // Parcourt le tableau des transformations et les ajoute à la liste des transformations
                    for (JsonElement transformationElement : transformationsArray) {
                        transformations.add(transformationElement.getAsString());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return transformations;
    }

    private void showDialog() {
        // Création de dialogue pour entrer le mot de passe
        Alert passwordDialog = new Alert(Alert.AlertType.CONFIRMATION);
        passwordDialog.setHeaderText("Entrez votre mot de passe :");

        // Ajouter un champ de saisie de mot de passe
        PasswordField passwordField = new PasswordField();
        passwordDialog.getDialogPane().setContent(passwordField);

        // Afficher la boîte de dialogue à l'utilisateur
        passwordDialog.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .ifPresent(response -> {
                    String password = passwordField.getText();
                    Image newimage = SecureRandomImageFilter.applyFilter(originalImage,password);
                    imageView.setImage(newimage);
                    transformationsList.add(new Transformation("mdp"));
                });
    }
}