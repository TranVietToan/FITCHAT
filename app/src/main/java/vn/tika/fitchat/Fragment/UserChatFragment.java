package vn.tika.fitchat.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import vn.tika.fitchat.Adapter.UserChatAdapter;
import vn.tika.fitchat.Model.Chat;
import vn.tika.fitchat.Model.User;
import vn.tika.fitchat.R;

public class UserChatFragment extends Fragment {
    View viewUserChat;

    private RecyclerView recyclerView;
    private UserChatAdapter userChatAdapter;
    private List<User> userChatList;

    EditText edtSearch;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    //Lưu tru id cac user da nhan va gui tin nhan cho user tai khoan dang duoc dang nhap
    private List<String> usersList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewUserChat = inflater.inflate(R.layout.fragment_user_chat, container, false);
        addControls();
        addEvents();
        return viewUserChat;
    }

    private void addControls() {
        recyclerView =viewUserChat.findViewById(R.id.rvUserChat);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        edtSearch = viewUserChat.findViewById(R.id.edtSearch);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        usersList = new ArrayList<>();
    }

    private void addEvents() {
        showUserChat();
        search();
    }
    private void showUserChat(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    if(chat.getReceiver().equals(firebaseUser.getUid())){
                        usersList.add(chat.getSender());
                    }
                    if(chat.getSender().equals(firebaseUser.getUid())){
                        usersList.add(chat.getReceiver());
                    }
                }

                getUserChat();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //Lấy ra nhưng user đã từng nhắn tin vơi tài khoản đang đăng nhập. Hiện ra recycle view.
    private void getUserChat(){
        userChatList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userChatList.clear();
                deleteDuplicateID(usersList);
                //snapshot.getChildren(): tra ve danh sach cac node trong "Users"
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    for(String userID: usersList){
                        if(user.getUserID().equals(userID)){
                            userChatList.add(user);
                        }
                    }
                }
//                Collections.reverse(userChatList);
                userChatAdapter = new UserChatAdapter(getContext(),userChatList, true);
                recyclerView.setAdapter(userChatAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    public void deleteDuplicateID(List<String> list){
        Set set = new HashSet<>(list);
        list.clear();
        list.addAll(set);
    }
    private void searchProcessing(String s){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("keySearch")
                .startAt(s).endAt(s+"uf88f");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!edtSearch.getText().toString().equals("")){
                    userChatList.clear();
                    for(DataSnapshot snap: snapshot.getChildren()){
                        User user = snap.getValue(User.class);
                        assert user != null;
                        assert firebaseUser != null;

                        if(!user.getUserID().equals(firebaseUser.getUid())){
                            userChatList.add(user);
                        }
                    }

                    userChatAdapter = new UserChatAdapter(getContext(), userChatList, false);
                    recyclerView.setAdapter(userChatAdapter);
                }else {
                    getUserChat();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void search(){
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchProcessing(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
