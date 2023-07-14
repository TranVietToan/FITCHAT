package vn.tika.fitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    EditText edtUserNameRegister, edtEmailRegister, edtPasswordRegister;
    Button btnRegister;
    TextView txtSignIn;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        addControls();
        addEvents();
    }

    private void addControls() {
        edtUserNameRegister = findViewById(R.id.edtUserNameRegister);
        edtEmailRegister = findViewById(R.id.edtEmailRegister);
        edtPasswordRegister = findViewById(R.id.edtPasswordRegister);
        btnRegister = findViewById(R.id.btnRegister);
        txtSignIn = findViewById(R.id.txtSignIn);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void addEvents() {
        onClickBtnRegister();
        onClickTxtSignIn();
    }

    //Tạo tài khoản
    private void register(String username, String email, String password){
        //ham nay dung xu dung de dk tai khoan

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            assert firebaseUser != null;
                            String userID = firebaseUser.getUid();

                            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("userID",userID);
                            hashMap.put("userName", username);
                            hashMap.put("nickName", username);
                            hashMap.put("avatar", "default");
                            hashMap.put("activeStatus","offline");
                            hashMap.put("keySearch",username.toLowerCase());
                            hashMap.put("studentCode","Chưa cập nhập");
                            hashMap.put("className","Chưa cập nhập");
                            hashMap.put("address","Chưa cập nhập");
                            hashMap.put("specialized","Chưa cập nhập");


                            //ghi dữ liệu user
                            databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Intent intent  = new Intent(RegisterActivity.this,MainActivity.class);
                                        //khi hoạt động bắt đầu, tất cả các hoạt động khác hiện tại sẽ bị hủy bỏ
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(RegisterActivity.this, "Homie khum đăng kí được" +
                                    " thử lại không thì tự tìm cách khác nhé", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //Xử lý đăng kí tài khoản
    private void onClickBtnRegister() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String regGmail = "@students.hou.edu.vn";
                String userName = edtUserNameRegister.getText().toString();
                String email = edtEmailRegister.getText().toString();
                String password = edtPasswordRegister.getText().toString();

                if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterActivity.this, "Nhập đủ hết các trường thông tin đi Homie. Mình xử bạn đấy", Toast.LENGTH_SHORT).show();
                }else if(password.length()<6){
                    Toast.makeText(RegisterActivity.this, "Mật khẩu dễ thế cũng đặt, đặt khó hơn đi bạn ơi", Toast.LENGTH_SHORT).show();
                }else if(email.contains(regGmail)==false){
                    Toast.makeText(RegisterActivity.this, "Đăng nhập bằng tài khoản của trường nha!!!", Toast.LENGTH_SHORT).show();
                }else {
                    register(userName, email, password);
                }
            }
        });
    }

    //Xử lý chuyển sang trang đăng nhập
    private void onClickTxtSignIn() {
        txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }
}