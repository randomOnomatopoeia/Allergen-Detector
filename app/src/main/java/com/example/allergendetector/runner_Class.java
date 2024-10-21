package com.example.allergendetector;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;


public class runner_Class extends AppCompatActivity {


    private static TextView showAllergens;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_navigation);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }


    public static void addAllergen(ArrayList<Allergens> allergies, Context context, TextView showAllergens) {
        // initialize inflater, Builder, and activity_input.xml file
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View allergenPopup = inflater.inflate(R.layout.activity_input, null);

        // define input areas from allergenPopup
        EditText allergenName = allergenPopup.findViewById(R.id.editTextAllergy);
        //CheckBox severity = allergenPopup.findViewById(R.id.checkSeverity);

        // show popup and get values from the user
        builder.setView(allergenPopup)
                // add submit button that takes user input to display and add to ArrayList<Allergens> allergies
                .setPositiveButton("Submit", ((dialog, which) -> {
                    String allergenText = allergenName.getText().toString();
                    String result = "Allergies: \n";

                    Allergens allergy = new Allergens(allergenText);
                    allergies.add(allergy);

                    // for each Allergens in allergies, add it to String results to be displayed
                    for (int i = 0; i < allergies.size(); i++) {
                        result += allergies.get(i).toString() + "\n";

                    }
                    showAllergens.setText(result);

                }))

                // add cancel button that cancels adding an allergen
                .setNegativeButton("Cancel", ((dialog, which) -> {
                    dialog.dismiss();

                }))

                // show the popup
                .show();


    }
    public static void deleteAllergen(ArrayList<Allergens> allergies, Context context) {

        // initialize inflater, Builder, and activity_input.xml file
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View allergenPopup = inflater.inflate(R.layout.activity_delete, null);

        // define input areas from allergenPopup
        EditText allergenName = allergenPopup.findViewById(R.id.editTextAllergy);

        // show popup and get values from the user
        builder.setView(allergenPopup)
                // add submit button that takes user input to display and deletes that index from ArrayList allergens
                .setPositiveButton("Submit", ((dialog, which) -> {
                    Integer allergenText = Integer.valueOf(allergenName.getText().toString());
                    allergies.remove(allergenText-1);

                }))

                // add cancel button that cancels deleting an allergen
                .setNegativeButton("Cancel", ((dialog, which) -> {
                    dialog.dismiss();

                }))

                // show the popup
                .show();


    }



}