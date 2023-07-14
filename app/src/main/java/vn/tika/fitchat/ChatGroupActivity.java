package vn.tika.fitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.tika.fitchat.Adapter.ChatAdapter;
import vn.tika.fitchat.Adapter.GroupMessageAdapter;
import vn.tika.fitchat.Model.Chat;
import vn.tika.fitchat.Model.Group;
import vn.tika.fitchat.Model.GroupMessages;
import vn.tika.fitchat.Model.User;

public class ChatGroupActivity extends AppCompatActivity {
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    CircleImageView civGroupChatAvatar;
    TextView txtGroupChatName;

    ImageView imgSend_GroupChat;
    EditText edtContentMessage_GroupChat;


    GroupMessageAdapter groupMessageAdapter;
    List<GroupMessages> listChat;
    RecyclerView recyclerView;

    Toolbar toolBarGroupChat;
    Intent intent;
    String groupID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_group);
        addControls();
        addEvents();
    }

    private void addControls() {
        toolBarGroupChat = findViewById(R.id.toolbarGroupChat);
        setSupportActionBar(toolBarGroupChat);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        civGroupChatAvatar = findViewById(R.id.civGroupChatAvatar);
        txtGroupChatName = findViewById(R.id.txtGroupChatName);

        imgSend_GroupChat = findViewById(R.id.imgSend_GroupChat);
        edtContentMessage_GroupChat = findViewById(R.id.edtContentMessage_GroupChat);

        recyclerView = findViewById(R.id.rvChat_GroupChat);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(false);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void addEvents() {
        startMainActivity();
        getDataIntent();
        sendMessage();
    }

    private void startMainActivity() {
      toolBarGroupChat.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatGroupActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            }
        });
    }

    private void getDataIntent() {
        intent = getIntent();
        groupID = intent.getStringExtra("GroupID");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Groups").child(groupID);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Group group = snapshot.getValue(Group.class);
                txtGroupChatName.setText(group.getGroupName());
                if(group.getGroupAvatar().equals("default")){
                   civGroupChatAvatar.setImageResource(R.drawable.img_avatar_default);
                }else {
                    Glide.with(getApplicationContext()).load(group.getGroupAvatar()).into(civGroupChatAvatar);
                }

                getMessage();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private String getTimeNow(){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy h:mm a");
        String time= dateFormat.format(Calendar.getInstance().getTime());
        return time;
    }

    private void sendMessage(){
       imgSend_GroupChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = edtContentMessage_GroupChat.getText().toString();
                if(!TextUtils.isEmpty(msg)){
                    saveMessage(firebaseUser.getUid(),groupID,msg);
                }else {
                    Toast.makeText(ChatGroupActivity.this, "Đen cho bạn rồi. Khum gửi được nha", Toast.LENGTH_SHORT).show();
                }
                edtContentMessage_GroupChat.setText("");
            }
        });
    }
    //Luu du lieu vao firebase
    private void saveMessage(String sender, String groupID, String message){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("groupID", groupID);
        hashMap.put("groupMessage", message);
        hashMap.put("timeSend",getTimeNow());
        reference.child("GroupMessages").push().setValue(hashMap);
    }

    //lấy tin nhắn từ db , hiện thông tin qua rv
    private void getMessage(){

        listChat = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("GroupMessages");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listChat.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    String avatar="default";
                    GroupMessages groupMessages = dataSnapshot.getValue(GroupMessages.class);
                    if (groupMessages.getGroupID().equals(groupID)){
                        listChat.add(groupMessages);

                    }
                    groupMessageAdapter = new GroupMessageAdapter(ChatGroupActivity.this,listChat,avatar);
                    recyclerView.setAdapter(groupMessageAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}