package com.example.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.example.singiair.DBHelper;
import com.example.singiair.R;
import com.example.singiair.User;

import java.io.ByteArrayOutputStream;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editNameInput, editSurnameInput, editEmailInput, editUsernameInput;
    private Button editSaveButton, editCancelButton;
    private DBHelper DB;
    private User user;

    private static final int REQUEST_CODE_GALLERY = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Nhận thông tin người dùng từ Intent
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        // Khởi tạo database
        DB = new DBHelper(this);

        // Liên kết UI components
        initUIComponents();

        // Đặt thông tin hiện tại của người dùng vào giao diện
        populateUserData();

        // Xử lý sự kiện lưu thay đổi
        editSaveButton.setOnClickListener(v -> handleSaveButtonClick());

        // Xử lý sự kiện hủy thay đổi
        editCancelButton.setOnClickListener(v -> handleCancelButtonClick());

        // Chọn ảnh đại diện
    }

    // Khởi tạo các thành phần UI
    private void initUIComponents() {
        editNameInput = findViewById(R.id.editProfileNameInput);
        editSurnameInput = findViewById(R.id.editProfileSurnameInput);
        editEmailInput = findViewById(R.id.editProfileEmailInput);
        editUsernameInput = findViewById(R.id.editProfileUsernameInput);
        editSaveButton = findViewById(R.id.editProfileButtonSave);
        editCancelButton = findViewById(R.id.editProfileCancelButton);

        editNameInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        editSurnameInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
    }

    // Hiển thị thông tin người dùng
    private void populateUserData() {
        if (user != null) {
            editNameInput.setText(user.getUserName());
            editSurnameInput.setText(user.getUserSurname());
            editEmailInput.setText(user.getUserEmail());
            editUsernameInput.setText(user.getUserUsername());
        }
    }

    // Xử lý nút lưu thông tin
    private void handleSaveButtonClick() {
        String userName = editNameInput.getText().toString().trim();
        String userSurname = editSurnameInput.getText().toString().trim();
        String userUsername = editUsernameInput.getText().toString().trim();
        String email = editEmailInput.getText().toString().trim();

        if (userName.isEmpty() || userSurname.isEmpty() || userUsername.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ các trường thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Địa chỉ email không đúng định dạng", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isUsernameExist = DB.checkUsername(userUsername);
        if (!userUsername.equals(user.getUserUsername()) && isUsernameExist) {
            Toast.makeText(this, "Tên người dùng này đã tồn tại", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật tên và họ với chữ cái đầu viết hoa
        userName = capitalizeFirstLetter(userName);
        userSurname = capitalizeFirstLetter(userSurname);

        // Cập nhật thông tin người dùng trong cơ sở dữ liệu
        DB.updateUserInfo(user.getUserId(), userUsername, userName, userSurname, email,
                user.getUserPassword(), user.getUserPosition(), user.getIsLogged(), user.getUserMoney());

        // Cập nhật đối tượng người dùng
        user.setUserName(userName);
        user.setUserSurname(userSurname);
        user.setUserUsername(userUsername);
        user.setUserEmail(email);

        Toast.makeText(this, "Đã cập nhật hồ sơ thành công\n", Toast.LENGTH_SHORT).show();

        // Quay lại màn hình Profile
        navigateToProfileActivity();
    }

    // Xử lý nút hủy bỏ
    private void handleCancelButtonClick() {
        navigateToProfileActivity();
    }

    // Điều hướng về màn hình Profile
    private void navigateToProfileActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }

    // Yêu cầu chọn ảnh từ thư viện
    private void requestImageFromGallery() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_GALLERY);
    }

    // Kết quả sau khi người dùng cấp quyền
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY);
            } else {
                Toast.makeText(this, "Không có quyền truy cập thư viện\n", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // Chuyển đổi ảnh từ ImageView thành byte[]
    public static byte[] imageViewToByte(ImageView image) {
        try {
            Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        } catch (ClassCastException e) {
            return new byte[0];
        }
    }

    // Xử lý kết quả từ Intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    // Quay lại màn hình trước đó
    @Override
    public void onBackPressed() {
        navigateToProfileActivity();
    }

    // Chuyển đổi chuỗi để viết hoa chữ cái đầu
    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
}
