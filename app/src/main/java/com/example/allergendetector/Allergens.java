package com.example.allergendetector;

import androidx.annotation.NonNull;

public class Allergens {

    // name of allergen
    private String name;

    // if true, avoid traces of allergen
    private boolean severity;

    // add to Allergen ArrayList
    public Allergens (String name, boolean severity) {
        this.name = name;
        this.severity = severity;
    }

    public String getName() {
        return this.name;
    }

    public boolean getSeverity(){return this.severity;}

    @NonNull
    @Override
    public String toString() {
        if (getSeverity()) {
            return "Severe allergy to " + getName();
        } else{
            return "Non-severe allergy to " + getName();
        }
    }
}

