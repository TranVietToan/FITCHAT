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
import vn.tika.fitchat.Model.Chat;
import vn.tika.fitchat.Model.User;

public class ChatActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    CircleImageView civUserAvatarChat;
    TextView txtUserNameChat;

    ImageView imvSend;
    EditText edtContetnMessage;


    ChatAdapter chatAdapter;
    List<Chat> listChat;
    RecyclerView recyclerView;

    Toolbar toolBarChat;
    Intent intent;
    String userID;

    ValueEventListener seenListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        addControls();
        addEvents();
    }

    private void addControls() {
        toolBarChat = findViewById(R.id.toolbarChat);
        setSupportActionBar(toolBarChat);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        civUserAvatarChat = findViewById(R.id.civUserAvatarChat);
        txtUserNameChat = findViewById(R.id.txtUserNameChat);
        imvSend = findViewById(R.id.imgSend);
        edtContetnMessage = findViewById(R.id.edtContentMessage);

        recyclerView = findViewById(R.id.rvChat);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(false);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void addEvents() {
        //nhận dữ liệu thông tin user muon chat từ UserChatAdapter
        getDataIntent();
        //Chuyển sang màn hình Main
        startMainActivity();
        //Gửi tin nhắn, lưu msg vào database
        sendMessage();
        //Kiem tra tinh trang tin nhan
        seenMessage(userID);

    }

    private void getDataIntent() {
        intent = getIntent();
        userID = intent.getStringExtra("UserID");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                txtUserNameChat.setText(user.getUserName());
                if(user.getAvatar().equals("default")){
                    civUserAvatarChat.setImageResource(R.drawable.img_avatar_default);
                }else {
                    Glide.with(getApplicationContext()).load(user.getAvatar()).into(civUserAvatarChat);
                }
//                readMessage(firebaseUser.getUid(), userID, user.getAvatar());
                getMessage(user.getAvatar());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //lấy tin nhắn từ db , hiện thông tin qua rv
    private void getMessage(String avatarUser){
        listChat = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listChat.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userID) ||
                            chat.getReceiver().equals(userID) && chat.getSender().equals(firebaseUser.getUid())){
                        listChat.add(chat);
                    }

                    chatAdapter = new ChatAdapter(ChatActivity.this,listChat,avatarUser);
                    recyclerView.setAdapter(chatAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void startMainActivity() {
        toolBarChat.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            }
        });
    }

    private String getTimeNow(){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy h:mm a");
        String time= dateFormat.format(Calendar.getInstance().getTime());
        return time;
    }

    //Luu du lieu vao firebase
    private void saveMessage(String sender, String receiver, String message){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("statusMessage", false);
        hashMap.put("timeSend",getTimeNow());
        reference.child("Chats").push().setValue(hashMap);
    }

    private void sendMessage(){
        imvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = edtContetnMessage.getText().toString();
                if(!TextUtils.isEmpty(msg)){
                    saveMessage(firebaseUser.getUid(),userID,msg);
                }else {
                    Toast.makeText(ChatActivity.this, "Đen cho bạn rồi. Khum gửi được nha", Toast.LENGTH_SHORT).show();
                }
                edtContetnMessage.setText("");
            }
        });
    }
    // sử lý xem người chat đã đọc tin chưa
    private void seenMessage(final String userID){
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userID)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("statusMessage", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //thiết lập trang thái hoạt đọng
    private void setActiveStatus(String status){
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("activeStatus", status);

        databaseReference.updateChildren(hashMap);
    }
    @Override
    protected void onResume() {
        super.onResume();
        setActiveStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        databaseReference.removeEventListener(seenListener);
        setActiveStatus("offline");
    }
}