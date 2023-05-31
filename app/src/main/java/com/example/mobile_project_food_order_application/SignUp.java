package com.example.mobile_project_food_order_application;

import android.app.ProgressDialog;
import android.icu.text.StringPrepParseException;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile_project_food_order_application.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUp extends AppCompatActivity {
    EditText edtPhone, edtName, edtPassword;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        edtPhone = findViewById(R.id.edtPhone);
        edtName = findViewById(R.id.edtName);
        edtPassword = findViewById(R.id.edtPassword);

        btnSignUp = findViewById(R.id.btnSignUp);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
                mDialog.setMessage("Vui lòng chờ...");
                mDialog.show();

                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name, phone, password;
                        name = String.valueOf(edtName.getText());
                        phone = "0" + edtPhone.getText();
                        password = String.valueOf(edtPassword.getText());
                        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(phone) && TextUtils.isEmpty(password)) {
                            mDialog.dismiss();
                            Toast.makeText(SignUp.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (TextUtils.isEmpty(name)) {
                            mDialog.dismiss();
                            Toast.makeText(SignUp.this, "Vui lòng nhập họ tên", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (TextUtils.isEmpty(phone)) {
                            mDialog.dismiss();
                            Toast.makeText(SignUp.this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (TextUtils.isEmpty(password)) {
                            mDialog.dismiss();
                            Toast.makeText(SignUp.this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //check if user not exist in database
                        if (dataSnapshot.child(phone).exists()) {
                            //get user information
                            mDialog.dismiss();
                            Toast.makeText(SignUp.this, "Số điện thoại đã được đăng ký!", Toast.LENGTH_SHORT).show();
                            return;

                        } else {
                            mDialog.dismiss();
                            User user = new User(name, password);
                            table_user.child(phone).setValue(user);
                            Toast.makeText(SignUp.this, "Đăng ký tài khoản thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
