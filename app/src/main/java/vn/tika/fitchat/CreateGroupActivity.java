package vn.tika.fitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import vn.tika.fitchat.Adapter.ChooseUserAdapter;
import vn.tika.fitchat.Adapter.UserGroupAdapter;
import vn.tika.fitchat.Model.Group;
import vn.tika.fitchat.Model.User;

public class CreateGroupActivity extends AppCompatActivity {
    ListView lvUserSuggest;
    ChooseUserAdapter chooseUserAdapter;
    ArrayList<User> userArrayList;

    RecyclerView rvUserGroup;
    UserGroupAdapter userGroupAdapter;
    ArrayList<User> userGroupArrayList;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    EditText edtNameGroup;
    Button btnCreteGroupConfirmation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        addControls();
        addEvents();
    }

    private void addControls() {
        lvUserSuggest = findViewById(R.id.lvUserSuggest);
        rvUserGroup = findViewById(R.id.rvUserGroup);

        edtNameGroup = findViewById(R.id.edtNameGroup);
        btnCreteGroupConfirmation = findViewById(R.id.btnCreteGroupConfirmation);
    }

    private void addEvents() {
        initListViewUserSuggest();
        initRVUserGroup();
        addDataByLvUserSuggest();
        onClickItemListViewUserSuggest();
        createGroup();
    }

    private String getTimeNow(){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy h:mm a");
        String time= dateFormat.format(Calendar.getInstance().getTime());
        return time;
    }
    private String getIDGroup(){
        DateFormat dateFormat = new SimpleDateFormat("ddMMyyyyhmma");
        String time= dateFormat.format(Calendar.getInstance().getTime());
        return time;
    }

    private void createGroup() {
        btnCreteGroupConfirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = getIDGroup();
                String groupName = edtNameGroup.getText().toString();
                String numberUser = String.valueOf(userArrayList.size());
                //Them cac thuoc tinh co ban cua group
                databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("groupID",id);
                hashMap.put("groupName",groupName);
                hashMap.put("numberUser", numberUser);
                hashMap.put("timeCreateGroup", getTimeNow());
                hashMap.put("createdBy", firebaseUser.getUid());
                hashMap.put("groupAvatar","default");
                databaseReference.child(id).setValue(hashMap);
                // them cac thanh vien vao trong group
                HashMap<String, Object> hashMapUser = new HashMap<>();
                hashMapUser.put("userID",firebaseUser.getUid());
                databaseReference.child(id).child("userGroup").push().setValue(hashMapUser);
                for(int i=0;i<userGroupArrayList.size();i++){
                    hashMapUser.put("userID",userGroupArrayList.get(i).getUserID());
                    databaseReference.child(id).child("userGroup").push().setValue(hashMapUser);
                }


                startActivity(new Intent(CreateGroupActivity.this, MainActivity.class));

            }


        });
    }


    private void initRVUserGroup() {
        rvUserGroup.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CreateGroupActivity.this,LinearLayoutManager.HORIZONTAL,false);
        rvUserGroup.setLayoutManager(linearLayoutManager);

        userGroupArrayList = new ArrayList<>();
        // Them tai khoan hien tai vao nhom chat
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    if(user.getUserID().equals(firebaseUser.getUid())){
                        userGroupArrayList.add(new User(user.getUserName(), user.getAvatar()));
                    }
                }
                userGroupAdapter = new UserGroupAdapter(CreateGroupActivity.this, userGroupArrayList);
                rvUserGroup.setAdapter(userGroupAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // xử ly khi click vao item
    // thong tin ve user duoc chon se duoc add vao list user tham gia group
    private void onClickItemListViewUserSuggest() {
        lvUserSuggest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String mUserID = userArrayList.get(i).getUserID();
                String mUserName = userArrayList.get(i).getUserName();
                String mAvatar = userArrayList.get(i).getAvatar();
                userGroupArrayList.add(new User(mUserID,mUserName,mAvatar));
                userGroupAdapter.notifyDataSetChanged();
            }
        });

    }

    private void initListViewUserSuggest() {
        userArrayList = new ArrayList<>();
        chooseUserAdapter = new ChooseUserAdapter(CreateGroupActivity.this, R.layout.layout_item_choose_user, userArrayList);
        lvUserSuggest.setAdapter(chooseUserAdapter);
    }
    private void addDataByLvUserSuggest(){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userArrayList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    if(!user.getUserID().equals(firebaseUser.getUid())){
                        userArrayList.add(user);
                    }
                }
                chooseUserAdapter.notifyDataSetChanged();
                lvUserSuggest.setAdapter(chooseUserAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}