package vn.tika.fitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import vn.tika.fitchat.Model.User;

public class EditProfileActivity extends AppCompatActivity {
    Toolbar toolbar;
    EditText edtEditUserName, edtEditNickName;
    EditText edtEditClassName, edtEditAddress;

    Spinner spEditSpecialized;
    ArrayAdapter<String> arrayAdapterSpecialized;
    ArrayList<String> arrayListSpecialized;

    Button btnUpdateConfirmation;

    String specialized;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        addControls();
        addEvents();
    }

    private void addControls() {
        toolbar = findViewById(R.id.toolbarEditProfile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edtEditUserName = findViewById(R.id.edtEditUserName);
        edtEditNickName = findViewById(R.id.edtEditNickName);
        edtEditAddress = findViewById(R.id.edtEditAddress);
        edtEditClassName = findViewById(R.id.edtEditClassName);
        spEditSpecialized = findViewById(R.id.spEditSpecialized);
        btnUpdateConfirmation = findViewById(R.id.btnUpdateConfirmation);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
    }

    private void addEvents() {
        getDataSpinnerSpecialized();
        loadDataUserOld();
        saveUpdateData();
        startMainActivity();
    }

    public void getDataSpinnerSpecialized(){
        arrayListSpecialized = new ArrayList<>();
        arrayListSpecialized.add("Công nghệ phần mềm");
        arrayListSpecialized.add("Đa phương tiên");
        arrayListSpecialized.add("Mạng và bảo mật");
        arrayListSpecialized.add("Chưa vào chuyên nghành");
        arrayAdapterSpecialized = new ArrayAdapter<>(EditProfileActivity.this,
                android.R.layout.simple_spinner_item, arrayListSpecialized);
        arrayAdapterSpecialized.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEditSpecialized.setAdapter(arrayAdapterSpecialized);

        spEditSpecialized.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                specialized = arrayListSpecialized.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void loadDataUserOld(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                edtEditUserName.setText(user.getUserName());
                edtEditNickName.setText(user.getNickName());
                edtEditAddress.setText(user.getAddress());
                edtEditClassName.setText(user.getClassName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void saveUpdateData(){
        btnUpdateConfirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("userName",edtEditUserName.getText().toString());
                hashMap.put("nickName",edtEditNickName.getText().toString());
                hashMap.put("className",edtEditClassName.getText().toString());
                hashMap.put("address",edtEditAddress.getText().toString());
                hashMap.put("specialized",specialized);

                databaseReference.updateChildren(hashMap);

                startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
            }
        });
    }

    private void startMainActivity() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditProfileActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            }
        });
    }
}