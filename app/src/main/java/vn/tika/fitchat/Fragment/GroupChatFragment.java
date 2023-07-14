package vn.tika.fitchat.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import vn.tika.fitchat.Adapter.GroupChatAdapter;
import vn.tika.fitchat.Adapter.UserChatAdapter;
import vn.tika.fitchat.Model.Group;
import vn.tika.fitchat.Model.User;
import vn.tika.fitchat.R;

public class GroupChatFragment extends Fragment {
    View viewGroupChat;

    private RecyclerView rvGroupChat;
    private GroupChatAdapter groupChatAdapter;
    private List<Group> groupChatList;


    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroupChat = inflater.inflate(R.layout.fragment_group_chat, container, false);
        addControls();
        addEvents();
        return viewGroupChat;
    }

    private void addControls() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        rvGroupChat = viewGroupChat.findViewById(R.id.rvGroupChat);
        rvGroupChat.setHasFixedSize(true);
        rvGroupChat.setLayoutManager(new LinearLayoutManager(getContext()));
    }
    private void addEvents() {
        initRvGroupChat();
        loadGroupChat();
    }

    private void initRvGroupChat() {
        groupChatList = new ArrayList<>();
//        groupChatList.add(new Group("Android","default"));
        groupChatAdapter = new GroupChatAdapter(getContext(),groupChatList );
        rvGroupChat.setAdapter(groupChatAdapter);
    }

    private void loadGroupChat() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupChatList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Group group = dataSnapshot.getValue(Group.class);
                    getGroupChat(group);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    // lay cac nhom ma tai khoan hien tai dang lam thanh vien
    public void getGroupChat(Group group){
        databaseReference.child(group.getGroupID()).child("userGroup")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                            User user = dataSnapshot.getValue(User.class);
                            if(firebaseUser.getUid().equals(user.getUserID())){
                                groupChatList.add(group);
                                groupChatAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


}
