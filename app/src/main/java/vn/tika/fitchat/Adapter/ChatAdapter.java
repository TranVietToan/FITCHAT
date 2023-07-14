package vn.tika.fitchat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import vn.tika.fitchat.Model.Chat;
import vn.tika.fitchat.R;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    View viewChat;
    public static  final int MSG_TYPE_LEFT = 0;
    public static  final int MSG_TYPE_RIGHT = 1;

    FirebaseUser firebaseUser;

    DatabaseReference databaseReference;

    private Context mContext;
    private List<Chat> listChat;
    private String avatarUserChat;

    public ChatAdapter(Context mContext, List<Chat> listChat, String avatarUserChat) {
        this.mContext = mContext;
        this.listChat = listChat;
        this.avatarUserChat = avatarUserChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            viewChat = LayoutInflater.from(mContext).inflate(R.layout.layout_chat_item_right, parent, false);
            return new ChatAdapter.ViewHolder(viewChat);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_chat_item_left, parent, false);
            return new ChatAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = listChat.get(position);

        holder.txtMessage.setText(chat.getMessage());

        if (avatarUserChat.equals("default")){
            holder.civAvatar.setImageResource(R.drawable.img_avatar_default);
        } else {
            Glide.with(mContext).load(avatarUserChat).into(holder.civAvatar);
        }
        if (position == listChat.size()-1){
            if(chat.isStatusMessage()==true){
                holder.txtSeen.setText("Seen");
            } else {
                holder.txtSeen.setText("Delivered");
            }
        } else {
            holder.txtSeen.setVisibility(View.GONE);
        }

        holder.txtMessage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(view.getContext(), chat.getTimeSend(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return listChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtMessage;
        public CircleImageView civAvatar;
        public TextView txtSeen;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            civAvatar = itemView.findViewById(R.id.civAvatar);
            txtSeen = itemView.findViewById(R.id.txtSeen);

        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (listChat.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
