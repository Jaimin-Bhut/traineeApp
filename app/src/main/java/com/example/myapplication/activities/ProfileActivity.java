package com.example.myapplication.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.model.User;
import com.example.myapplication.sql.DatabaseHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ProfileActivity extends AppCompatActivity {
    private EditText editTextfname, editTextlname, editTextemail, editTextPassword;
    private RadioButton radioButton;
    private RadioGroup radioGender;
    private Button btnUpdate;
    private User user;
    public DatabaseHelper databaseHelper;
    private ImageView imageViewProfilePic;
    private String TAG = "ProfileActivity", currentmail;
    private final int PICK_IMAGE_CAMERA = 0, PICK_IMAGE_GALLERY = 1, REQUEST_CODE = 2;
    private Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        radioGender = findViewById(R.id.radioGroupGender);
        editTextfname = findViewById(R.id.editTextFname);
        editTextlname = findViewById(R.id.editTextLname);
        editTextemail = findViewById(R.id.editTextEmail);
        btnUpdate = findViewById(R.id.btnUpdate);
        editTextPassword = findViewById(R.id.editTextPassword);
        imageViewProfilePic = findViewById(R.id.imgProfilePic1);
        databaseHelper = new DatabaseHelper(this);
        user = new User();
        databaseHelper.checkUser(editTextemail.getText().toString());
        imageViewProfilePic = findViewById(R.id.imgProfilePic1);
        imageViewProfilePic.setOnClickListener(view -> onOpenImage());
        isDefaultValue();

        findViewById(R.id.btnUpdate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String fname = editTextfname.getText().toString();
                String lname = editTextlname.getText().toString();
                String email = editTextemail.getText().toString();
                String pass = editTextPassword.getText().toString();
                imageViewProfilePic = findViewById(R.id.imgProfilePic1);
                imageViewProfilePic.setOnClickListener(view1 -> onOpenImage());
                btnUpdate = findViewById(R.id.btnUpdate);
                Bitmap imageBtimap = ((BitmapDrawable) imageViewProfilePic.getDrawable()).getBitmap();

                if (!isValidFname(fname)) {
                    editTextfname.setError("Invalid");
                } else if (!isValidLname(lname)) {
                    editTextlname.setError("Invalid");
                } else if (!isValidEmail(email)) {
                    editTextemail.setError("Invalid");
                } else if (!isValidPassword(pass)) {
                    editTextPassword.setError("Invalid");
                } else {
                    int selectedId = radioGender.getCheckedRadioButtonId();
                    radioButton = findViewById(selectedId);
                    User user = new User();
                    user.setGender(radioButton.getText().toString());
                    user.setFirst_name(editTextfname.getText().toString());
                    user.setLast_name(editTextlname.getText().toString());
                    user.setEmail(editTextemail.getText().toString());
                    user.setPassword(editTextPassword.getText().toString());
                    user.setPhoto(imageBtimap);

                    if (databaseHelper.updateUser(user)) {

                        Intent intent = new Intent(ProfileActivity.this, SignInActivity.class);
                        startActivity(intent);
                        Toast.makeText(ProfileActivity.this, "Profile Updated Succesfully", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(ProfileActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        findViewById(R.id.btnCencel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileActivity.this.finish();
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case PICK_IMAGE_CAMERA:
                try {
                    if (selectedImage != null) {
                        Bitmap photoBitmap = BitmapFactory.decodeFile(selectedImage.getPath());
                        setImageData(photoBitmap);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "PICK_FROM_CAMERA" + e);
                }
                break;

            case PICK_IMAGE_GALLERY:
                try {
                    if (resultCode == RESULT_OK) {
                        Uri uri = imageReturnedIntent.getData();
                        if (uri != null) {
                            Bitmap bitmap = null;
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                            setImageData(bitmap);
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, "PICK_FROM_GALLERY" + e);
                }
                break;
        }
    }
    private void setImageData(Bitmap bitmap) {
        try {
            if (bitmap != null) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                imageViewProfilePic.setImageBitmap(decoded);
            } else {
                Log.e(TAG, "Unable to select image");
            }
        } catch (Exception e) {
            Log.e(TAG, "setImageData" + e);
        }
    }


    private void onOpenImage() {
        if (Build.VERSION.SDK_INT < 23) {
            selectImage();
        } else {
            if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission
                    .CAMERA) == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest
                        .permission.CAMERA, Manifest
                        .permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        }

    }

    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Select Option");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Take Photo")) {
                dialog.dismiss();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                selectedImage = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                        "image_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
                intent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImage);
                startActivityForResult(intent, PICK_IMAGE_CAMERA);
            } else if (options[item].equals("Choose From Gallery")) {
                dialog.dismiss();
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickPhoto.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(pickPhoto, "Compelete action using"),
                        PICK_IMAGE_GALLERY);
            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void goWithCameraPermission(int[] grantResults) {
        if (grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectImage();
        } else if (grantResults[1] == PackageManager.PERMISSION_DENIED ||
                grantResults[0] == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest
                    .permission.WRITE_EXTERNAL_STORAGE, Manifest
                    .permission.CAMERA}, REQUEST_CODE);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0) {
            switch (requestCode) {
                case REQUEST_CODE:
                    goWithCameraPermission(grantResults);
                    break;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean isValidFname(String fname) {
        if (fname.length() > 2) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isValidLname(String lname) {
        if (lname.length() > 2) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isValidEmail(String email) {
        String Email_Pattern = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        Pattern pattern = Pattern.compile(Email_Pattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidPassword(String pass) {
        if (pass.length() > 1) {
            return true;
        } else {
            return false;
        }
    }

    private void isDefaultValue() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String email = sharedPreferences.getString("email", "");
        user = databaseHelper.getUserDataByEmail(email);
        editTextfname.setText(user.getFirst_name());
        editTextlname.setText(user.getLast_name());
        editTextemail.setText(user.getEmail());
        imageViewProfilePic.setImageBitmap(user.getPhoto());
    }

}
