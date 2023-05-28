package com.example.handlestorage;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    int myRequestCode = 100;

    EditText inputText;
    TextView response;
    Button saveButton, readButton;

    private final String filename = "SampleFile.txt";
    private final String filepath = "MyFileStorage";
    File myExternalFile;
    String myData = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputText = findViewById(R.id.myInputText);
        response = findViewById(R.id.response);
        saveButton = findViewById(R.id.saveExternalStorage);

        checkPermissionRequest();

    }

    void checkPermissionRequest() {
        // Check if the permission is already granted
        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            // Request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE},
                    myRequestCode);
        } else {
            // Permission already granted
            // Your code for accessing storage here
            setupFileStorage();
        }
    }

    ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    setupFileStorage();
                } else {
                    Log.d(TAG, "storagePermission: Not Granted");
                    Toast.makeText(this, "Permission Required", Toast.LENGTH_SHORT).show();
                }
            }
    );


    void setupFileStorage() {

        saveButton.setOnClickListener(v -> {
            try {
                FileOutputStream fos = new FileOutputStream(myExternalFile);
                fos.write(inputText.getText().toString().getBytes());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputText.setText("");
            response.setText("SampleFile.txt saved to External Storage...");
        });

        readButton = findViewById(R.id.getExternalStorage);
        readButton.setOnClickListener(v -> {
            try {
                FileInputStream fis = new FileInputStream(myExternalFile);
                DataInputStream in = new DataInputStream(fis);
                BufferedReader br =
                        new BufferedReader(new InputStreamReader(in));
                String strLine;
                while ((strLine = br.readLine()) != null) {
                    myData = /*myData +*/ strLine;
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputText.setText(myData);
            response.setText("SampleFile.txt data retrieved from Internal Storage...");
        });

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            saveButton.setEnabled(false);
        } else {
            myExternalFile = new File(getExternalFilesDir(filepath), filename);
        }
    }

    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState);
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(extStorageState);
    }
}