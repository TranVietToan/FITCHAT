package vn.tika.fitchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.tika.fitchat.ChatActivity;
import vn.tika.fitchat.ChatGroupActivity;
import vn.tika.fitchat.Model.Group;
import vn.tika.fitchat.Model.User;
import vn.tika.fitchat.R;

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.ViewHolder>{

    private Context context;
    private List<Group> listGroup;

    public GroupChatAdapter(Context context, List<Group> listGroup) {
        this.context = context;
        this.listGroup = listGroup;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_group_item, parent,false);
        return new GroupChatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Group group = listGroup.get(position);
        holder.txtGroupChatName.setText(group.getGroupName());
        Toast.makeText(context, group.getNumberUser(), Toast.LENGTH_SHORT).show();
        if(group.getGroupAvatar().equals("default")){

            holder.civAvatarGroupChat.setImageResource(R.drawable.img_avatar_default);
        }
        else {
            Glide.with(context).load(group.getGroupAvatar()).into(holder.civAvatarGroupChat);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatGroupActivity.class);
                intent.putExtra("GroupID", group.getGroupID());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listGroup.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtGroupChatName;
        public CircleImageView civAvatarGroupChat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtGroupChatName = itemView.findViewById(R.id.txtUserGroupChatName);
            civAvatarGroupChat = itemView.findViewById(R.id.civAvatarGroupChat);

        }
    }
}
