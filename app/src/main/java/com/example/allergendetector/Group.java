package com.example.allergendetector;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Group extends Allergens {

    private ArrayList<String> type;


    // constructor for specific nut allergies
    public Group(ArrayList<String> type, boolean traces, String name) {
        super(name, traces);
        this.type = type;

        // if traces, then add "tree nuts" (lowercase of name) in order to compare for traces
        if (traces) {
            this.type.add(name.toLowerCase());
        }
    }

    //assume all tree nuts if no type
    public Group(boolean traces, String name) {
        super(name, traces);
        this.type = new ArrayList<String>();

        // if traces, then add "tree nuts" (lowercase of name) in order to compare for traces
        if (traces) {
            this.type.add(name.toLowerCase());
        }
    }

    @NonNull
    @Override
    public String toString() {
        //initialize string
        String val = "";
        //add each allergen from type if type exists
        if (!type.isEmpty()) {
            for (String i : type) {
                val += i + ", ";
            }
        }
        val = getName();
        return val;
    }
}
