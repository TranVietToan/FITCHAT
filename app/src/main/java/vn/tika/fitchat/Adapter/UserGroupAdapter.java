package vn.tika.fitchat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.tika.fitchat.Model.User;
import vn.tika.fitchat.R;

public class UserGroupAdapter extends RecyclerView.Adapter<UserGroupAdapter.ViewHolder> {
    private Context context;
    private List<User> listUserGroup;

    public UserGroupAdapter(Context context, List<User> listUserGroup) {
        this.context = context;
        this.listUserGroup = listUserGroup;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View item = layoutInflater.inflate(R.layout.layout_item_user_group,parent,false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = listUserGroup.get(position);
        holder.txtUserGroupName.setText(user.getUserName());
        if(user.getAvatar().equals("default")){
            holder.civAvatarUserGroup.setImageResource(R.drawable.img_avatar_default);
        }else {
            Glide.with(context).load(user.getAvatar()).into(holder.civAvatarUserGroup);
        }

    }

    @Override
    public int getItemCount() {
        return listUserGroup.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView civAvatarUserGroup;
        TextView txtUserGroupName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserGroupName = itemView.findViewById(R.id.txtUserGroupName);
            civAvatarUserGroup = itemView.findViewById(R.id.civAvatarUserGroup);
        }
    }
}
