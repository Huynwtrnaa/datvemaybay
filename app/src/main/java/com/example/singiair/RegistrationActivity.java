package com.example.singiair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class RegistrationActivity extends AppCompatActivity {

    EditText registrationName, registrationSurname, registrationUsername, registrationEmail, registrationPassword;
    Button registrationConfirmButton, registrationExistingUserButton;
    DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //Input information for registration new user
        registrationName = (EditText) findViewById(R.id.registrationInputName);
        registrationName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES); // Set first latter capital
        registrationSurname = (EditText) findViewById(R.id.registrationInputSurname);
        registrationSurname.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES); // Set first latter capital
        registrationUsername = (EditText) findViewById(R.id.registrationInputUsername);
        registrationEmail = (EditText) findViewById(R.id.registrationInputEmail);
        registrationPassword = (EditText) findViewById(R.id.registrationInputPassword);

        //Button in registration form
        registrationConfirmButton = (Button) findViewById(R.id.registrationConfirmButton);
        registrationExistingUserButton = (Button) findViewById(R.id.registrationUserExistingButton);

        //Instance database
        DB = new DBHelper(this);

        //Registration
        registrationConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userName = registrationName.getText().toString();
                String userSurname = registrationSurname.getText().toString();
                String userUsername = registrationUsername.getText().toString();
                String userEmail = registrationEmail.getText().toString();
                String userPassword = registrationPassword.getText().toString();

                if(userName.equals("") || userSurname.equals("") || userUsername.equals("") || userEmail.equals("") || userPassword.equals("")){
                    Toast.makeText(RegistrationActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
                    Toast.makeText(RegistrationActivity.this, "Địa chỉ email không đúng định dạng", Toast.LENGTH_SHORT).show();
                }
                else {
                    //Check if that user already exists in the database
                    Boolean checkUser = DB.checkUsername(userUsername);
                    if (checkUser == false) {
                        userName = userName.substring(0,1).toUpperCase() + userName.substring(1).toLowerCase();
                        userSurname = userSurname.substring(0,1).toUpperCase() + userSurname.substring(1).toLowerCase();

                        Boolean insert = DB.insertData(userName, userSurname, userUsername, userEmail, userPassword, "user", false,0);
                        if (insert == true) {
                            Toast.makeText(RegistrationActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                            registrationName.setText("");
                            registrationSurname.setText("");
                            registrationUsername.setText("");
                            registrationEmail.setText("");
                            registrationPassword.setText("");
                            //Go user to login with new acc
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegistrationActivity.this, "Đăng ký không thành công", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Tên người dùng này đã tồn tại", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //Login
        registrationExistingUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 999){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //Gallery intent
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 999);
            }
            else {
                Toast.makeText(this, "Không có quyền truy cập", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        DBHelper MyDB = new DBHelper(this);
        startActivity(intent);
        finish();
    }
}