package com.example.myapplication.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.model.User;
import com.example.myapplication.sql.DatabaseHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;


public class SignUpActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_CAMERA = 0;
    private static final int PICK_IMAGE_GALLERY = 1;
    private TextView textView;
    private Uri selectedImage;

    private EditText editTextFname, editTextLname, editTextEmail, editTextPass, editTextConfirmPass;
    private RadioButton radioButton;
    private RadioGroup radioGender;
    public DatabaseHelper databaseHelper;
    private ImageView imageViewProfilePic;
    private String TAG="SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        radioGender = findViewById(R.id.rgGender);
        editTextFname = findViewById(R.id.etFname);
        editTextLname = findViewById(R.id.etLname);
        editTextEmail = findViewById(R.id.etEmail);
        editTextPass = findViewById(R.id.etPass);
        editTextConfirmPass = findViewById(R.id.etCpass);
        imageViewProfilePic=findViewById(R.id.imgProfilePic);
        databaseHelper = new DatabaseHelper(this);

        editTextFname.setText("Jaimin");
        editTextLname.setText("Bhut");
        editTextEmail.setText( "jaimin@gmail.com" );
        editTextPass.setText( "1234567" );
        editTextConfirmPass.setText( "1234567" );

        findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Fname = editTextFname.getText().toString();
                String Lname = editTextLname.getText().toString();
                String Email = editTextEmail.getText().toString();
                String Pass = editTextPass.getText().toString();
                String Cpass = editTextConfirmPass.getText().toString();
                Bitmap imageBtimap = ((BitmapDrawable) imageViewProfilePic.getDrawable()).getBitmap();

                if (!isValidFname(Fname)) {
                    editTextFname.setError("Invalid First Name");
                } else if (!isValidLname(Lname)) {
                    editTextLname.setError("Invalid Last Name");
                } else if (!isValidEmail(Email)) {
                    editTextEmail.setError("Invalid Email");
                } else if (!isValidPass(Pass)) {
                    editTextPass.setError("Invalid");
                } else if (!isValidConfirmPass(Cpass, Pass)) {
                    editTextConfirmPass.setError("Not Match Password");
                } else if (databaseHelper.checkUser(Email)) {
                    Toast.makeText(SignUpActivity.this, "Email are already exist", Toast.LENGTH_SHORT).show();
                } else{
                    int selectedId = radioGender.getCheckedRadioButtonId();
                    radioButton = findViewById(selectedId);
                    User user = new User();
                    user.setGender(radioButton.getText().toString());
                    user.setFirst_name(editTextFname.getText().toString());
                    user.setLast_name(editTextLname.getText().toString());
                    user.setEmail(editTextEmail.getText().toString());
                    user.setPassword(editTextPass.getText().toString());
                    user.setPhoto(imageBtimap);

                    if (databaseHelper.addUser(user)) {

                        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                        startActivity(intent);
                        Toast.makeText(SignUpActivity.this, "User Created Succesfully", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(SignUpActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        textView = findViewById(R.id.txtLogin);
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, SignUpActivity.class);
                startActivity(intent);
                return false;
            }
        });
    }
    public void onClickSelectPhoto(View view) {
        if (Build.VERSION.SDK_INT < 23) {
            selectImage();
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission
                    .CAMERA) == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                ActivityCompat.requestPermissions(SignUpActivity.this,
                        new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            switch (requestCode) {
                case 1:
                    goWithCameraPermission(grantResults);
                    break;
                default:
                    break;
            }
        }
    }

    private void goWithCameraPermission(int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectImage();
        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(SignUpActivity.this,
                    new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    /**
     * Opens PickImageDialog to choose,
     * Camera or Gallery
     */
    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SignUpActivity.this);
        builder.setTitle("Select Option");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Take Photo")) {
                dialog.dismiss();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                selectedImage = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                        "image_" + System.currentTimeMillis() + ".jpg"));
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (resultCode != RESULT_OK) return;
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



    private boolean isValidFname(String Fname) {
        if (Fname.length() > 2) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isValidLname(String Lname) {
        if (Lname.length() > 2) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isValidEmail(String Email) {
        String Email_Pattern = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        Pattern pattern = Pattern.compile(Email_Pattern);
        Matcher matcher = pattern.matcher(Email);
        return matcher.matches();
    }

    private boolean isValidPass(String Pass) {
        if (Pass.length() > 6) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isValidConfirmPass(String Cpass, String pass) {
        if (Cpass.equals(pass)) {
            return true;
        } else {
            return false;
        }
    }

}
