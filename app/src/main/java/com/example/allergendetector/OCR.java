package com.example.allergendetector;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
//import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.provider.MediaStore;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;


import java.util.ArrayList;


public class OCR extends AppCompatActivity {
    // initialize UI views variables

    public static ArrayList<Allergens> allergies = new ArrayList<Allergens>();
    private MaterialButton button_capture;
    private MaterialButton button_upload;
    private MaterialButton button_check;
    private MaterialButton add_allergens;
    private ShapeableImageView imageIv;
    private EditText editText;
    private TextView showAllergens;
    private TextView results;


    // to deal with results of camera/gallery intent
    private static final int REQUEST_CAMERA_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 101;
    private static final String TAG = "MAIN_TAG";

    //uri of image used
    private Uri imageUri = null;

    //arrays of permissions required
    private String[] cameraPermissions;

    //private ProgressBar progressBar;
    // TextRecognizer from the google vision package
    private TextRecognizer textRecognizer;

    // public ArrayList<String> for the OCR
    public ArrayList<String> ocrResults;

    @SuppressLint("QueryPermissionsNeeded")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // init and define UI elements
        button_check = findViewById(R.id.checkAllergies);
        button_capture = findViewById(R.id.button_capture);
        add_allergens = findViewById(R.id.add_allergens);
        imageIv = findViewById(R.id.imageIv);
        editText = findViewById(R.id.editText);
        results = findViewById(R.id.results);

        // initializing arrays of permissions
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        // handle clicking button_capture; translate image
        button_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputImageDialog();
            }
        });

        // Initialize TextRecognizer with options
        TextRecognizerOptions options = new TextRecognizerOptions.Builder().build();
        textRecognizer = TextRecognition.getClient(options);

        // handle button for checking allergies
        button_check.setOnClickListener(v -> {

            // check imageUri to see if image exists
            if (imageUri == null) {
                // show error message if no image is picked
                Toast.makeText(OCR.this, "Pick image", Toast.LENGTH_SHORT).show();

            } else {
                // if it exists, recognize the text
                recognizeTextFromImage();
            }
            String text = editText.getText().toString().trim();
            ocrResults = makeArray(text, true);
            checkAllergies(allergies, results);
        });

        // handle adding allergens by navigating to activity_navigation
        add_allergens.setOnClickListener(view -> {
            // Using Navigation component to navigate to a destination
            addAllergen();
        });

    }

    public static ArrayList<String> makeArray(String groupText, Boolean traces) {
        // make usableText variable to minimize mistakes
        String usableText = "";
        if (groupText.toLowerCase().contains("ingredients:") && groupText.contains(".")) {
            usableText = groupText.substring(groupText.indexOf(":") + 2, groupText.indexOf("."));
            if (groupText.toLowerCase().contains("may contain:") && traces) {

            }
        } else {
            usableText = groupText;
        }
        // split the string at each comma
        String[] splitArray = usableText.split("");
        // convert the resulting array into an ArrayList<String>
        ArrayList<String> result = new ArrayList<>();

        for (String item : splitArray) {
            result.add(item.trim());  // Trim to remove any extra spaces around each element
        }

        return result;

    }

    public void addAllergen() {
        PopupMenu popupMenu = new PopupMenu(this, button_capture);
        popupMenu.getMenu().add(Menu.NONE, 1, 1, "Add Allergy");
        popupMenu.getMenu().add(Menu.NONE, 2, 2, "See Allergies");
        popupMenu.getMenu().add(Menu.NONE, 3, 3, "Delete Allergy");
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // show activity_navigation as a popup
                AlertDialog.Builder builder = new AlertDialog.Builder(OCR.this);
                LayoutInflater inflater = getLayoutInflater();
                View allergenPopup = inflater.inflate(R.layout.activity_navigation, null);
                View allergenDelete = inflater.inflate(R.layout.activity_delete, null);

                // set showAllergens equal to text allergensList that shows allergies
                showAllergens = allergenPopup.findViewById(R.id.allergensList);

                // determine which button was pressed
                int id = item.getItemId();

                //if 1 is pressed, show popup for adding allergens and add allergy to arrayList allergies
                if (id == 1) {
                    runner_Class.addAllergen(allergies, OCR.this, showAllergens);
                // if 2 is pressed, show allergy list
                } else if (id == 2) {
                    // Update the showAllergens TextView before showing the dialog
                    StringBuilder allergensText = new StringBuilder();
                    for (int i = 1; i <= allergies.size(); i++) {
                        allergensText.append(i).append(". ").append(allergies.get(i-1)).append("\n");
                    }
                    showAllergens.setText(allergensText.toString());

                    // Set the updated view to the AlertDialog
                    builder.setView(allergenPopup);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    runner_Class.deleteAllergen(allergies, OCR.this);

                }
                return true;
            }
        });


    }

    private void checkAllergies(ArrayList<Allergens> allergens, TextView conclusion) {
        // initialize inflater
        LayoutInflater inflater = getLayoutInflater();
        // set view
        View OCR = inflater.inflate(R.layout.activity_main, null);

        // initialize boolean for allergy found, output String, and index of possible allergen
        boolean check = false;
        String result = "";
        int index = 0;

        //traverse through OCR list
        for (int i = 0; i < ocrResults.size(); i++) {
            // traverse through allergies list
            for (int k = 0; k < allergens.size(); k++) {
                String allergy = allergens.get(k).getName();
                // compare lowercase versions of each item in the list to the items in the ocr list
                if (allergy.equalsIgnoreCase(ocrResults.get(i))) {
                    check = true;
                    index = k;
                    break;
                }
            }
        }
        // if an allergy is found, print message + item
        if (check) {
            result += "You shouldn't eat this item because it was flagged for " + allergens.get(index).getName();
            conclusion.setText(result);
        } else {
            result += "You can eat this item because no allergies were flagged. Please read through if not sure.";
            conclusion.setText(result);
        }

    }

    private void recognizeTextFromImage() {
        Log.d(TAG, "recognizeTextFromImage: ");

        try {
            InputImage inputImage = InputImage.fromFilePath(this, imageUri);
            Task<Text> textTaskResult = textRecognizer.process(inputImage)
                    .addOnSuccessListener(text -> {
                        String recognizedText = text.getText();
                        Log.d(TAG, "onSuccess: recognizedText:" + recognizedText);
                        editText.setText(recognizedText);
                    }).addOnFailureListener(e -> {
                        Log.e(TAG, "onFailure: ", e);
                        Toast.makeText(OCR.this, "Failed:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        } catch (Exception e) {
            // exception occurred; show reason in Toast
            Log.e(TAG, "recognizeTextFromImage: ", e);

        }
    }

    private void showInputImageDialog() {
        pickImageGallery();
    }

    private void pickImageGallery() {
        Log.d(TAG, "pickImageGallery: Gallery Chosen");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if (o.getResultCode() == Activity.RESULT_OK) {
                        // image picked

                        assert o.getData() != null;
                        imageUri = o.getData().getData();
                        Log.d(TAG, "onActivityResult:" + imageUri);
                        //set to imageView
                        imageIv.setImageURI(imageUri);
                    } else {
                        // image not picked
                        Toast.makeText(OCR.this, "cancelled...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void pickImageCamera() {
        Log.d(TAG, "pickImageCamera: ");
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Sample Title");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Sample Description");

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
    }

    private final ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // we have image from camera
                        Log.d(TAG, "onActivityResult: " + imageUri);
                        imageIv.setImageURI(imageUri);
                    } else {
                        //cancelled
                        Log.d(TAG, "onActivityResult: cancelled");
                        Toast.makeText(OCR.this, "cancelled...", Toast.LENGTH_SHORT).show();
                    }
                }
            }

    );

    private boolean checkCameraPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
    }

    private void requestCameraPermissions() {
        // request camera permissions
        ActivityCompat.requestPermissions(this, cameraPermissions, REQUEST_CAMERA_CODE);
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA_CODE: {
                if (grantResults.length > 0) {
                    // check if storage and camera permissions are granted
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    // checks camera permissions
                    if (cameraAccepted) {
                        // runs method to pick image if camera permissions are given
                        pickImageCamera();
                        System.out.print("Hello");
                    } else {
                        // output if camera permissions are needed
                        Toast.makeText(this, "Camera permissions are required", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Camera permissions are required", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    // check storage permissions
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    // check if storage permissions are granted
                    if (storageAccepted) {
                        pickImageGallery();
                    } else {
                        Toast.makeText(this, "Storage permissions are required", Toast.LENGTH_SHORT).show();

                    }
                }
            }
            break;
        }


    }


}