package com.example.projet;

import java.util.ArrayList;
import java.util.List;

public class Metadata {
    public String imageName;
    public List<String> tags;
    public ArrayList<String> transformations;

    public Metadata(String imageName, List<String> tags, List<Transformation> transformationsList) {
        this.imageName = imageName;
        this.tags = tags;
        this.transformations = new ArrayList<>();
        for (Transformation transformation : transformationsList) {
            this.transformations.add(transformation.getType());
        }
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public ArrayList<String> getTransformations() {
        return transformations;
    }

    public void setTransformations(ArrayList<String> transformations) {
        this.transformations = transformations;
    }
}
