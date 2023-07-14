package vn.tika.fitchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.tika.fitchat.ChatActivity;
import vn.tika.fitchat.Model.Chat;
import vn.tika.fitchat.Model.User;
import vn.tika.fitchat.R;

public class UserChatAdapter extends RecyclerView.Adapter<UserChatAdapter.ViewHolder> {

    private Context context;
    private List<User> listUser;
    private boolean activeStatus;

    String lastMessage;

    public UserChatAdapter(Context context, List<User> listUser, boolean activeStatus) {
        this.context = context;
        this.listUser = listUser;
        this.activeStatus = activeStatus;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_user_item, parent,false);
        return new UserChatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = listUser.get(position);
        holder.txtUserName.setText(user.getUserName());
        if(user.getAvatar().equals("default")){
            holder.civAvatarUser.setImageResource(R.drawable.img_avatar_default);
        }else {
            Glide.with(context).load(user.getAvatar()).into(holder.civAvatarUser);
        }
        
        lastMessage(user.getUserID(), holder.txtLastMsg);

        if(activeStatus){
            if(user.getActiveStatus().equals("online")){
                holder.civActiveStatus.setVisibility(View.VISIBLE);
            }else {
                holder.civActiveStatus.setVisibility(View.GONE);
            }
        }else {
            holder.civActiveStatus.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("UserID", user.getUserID());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtUserName;
        public CircleImageView civAvatarUser;
        public CircleImageView civActiveStatus;
        public TextView txtLastMsg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            civAvatarUser= itemView.findViewById(R.id.civAvatarUser);

            civActiveStatus = itemView.findViewById(R.id.civActiveStatus);

            txtLastMsg = itemView.findViewById(R.id.txtLastMsg);
        }
    }
    public void lastMessage(String userID, TextView lastMsg){
        lastMessage = "default";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userID)
                    || chat.getReceiver().equals(userID) && chat.getSender().equals(firebaseUser.getUid())){
                        lastMessage = chat.getMessage();
                    }
                }
                switch (lastMessage){
                    case "default":{
                        lastMsg.setText("");
                        break;
                    }
                    default:{
                        lastMsg.setText(lastMessage);
                        break;
                    }
                }

                lastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
