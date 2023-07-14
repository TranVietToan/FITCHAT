package vn.tika.fitchat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.tika.fitchat.Model.Chat;
import vn.tika.fitchat.Model.GroupMessages;
import vn.tika.fitchat.R;

public class GroupMessageAdapter extends RecyclerView.Adapter<GroupMessageAdapter.ViewHolder>{

    View viewChat;
    public static  final int MSG_TYPE_LEFT = 0;
    public static  final int MSG_TYPE_RIGHT = 1;

    FirebaseUser firebaseUser;

    private Context mContext;
    private List<GroupMessages> listGroupMessage;
    private String avatarUserChat;

    public GroupMessageAdapter(Context mContext, List<GroupMessages> listGroupMessage, String avatarUserChat) {
        this.mContext = mContext;
        this.listGroupMessage = listGroupMessage;
        this.avatarUserChat = avatarUserChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            viewChat = LayoutInflater.from(mContext).inflate(R.layout.layout_chat_item_right, parent, false);
            return new GroupMessageAdapter.ViewHolder(viewChat);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_chat_item_left, parent, false);
            return new GroupMessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        Chat chat = listChat.get(position);
        GroupMessages groupMessages = listGroupMessage.get(position);
        holder.txtMessage.setText(groupMessages.getGroupMessage());

        if (avatarUserChat.equals("default")){
            holder.civAvatar.setImageResource(R.drawable.img_avatar_default);
        } else {
            Glide.with(mContext).load(avatarUserChat).into(holder.civAvatar);
        }
        holder.txtSeen.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return listGroupMessage.size();
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
        if (listGroupMessage.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
