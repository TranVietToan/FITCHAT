package vn.tika.fitchat.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.tika.fitchat.Model.User;
import vn.tika.fitchat.R;

public class ChooseUserAdapter extends ArrayAdapter {
    Activity context;
    int resource;
    List objects;
    public ChooseUserAdapter(@NonNull Activity context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = this.context.getLayoutInflater();
        View rowItem = layoutInflater.inflate(this.resource, null);
        CircleImageView civAvatarUserChoose = rowItem.findViewById(R.id.civAvatarUserChoose);
        TextView txtUserNameChoose = rowItem.findViewById(R.id.txtUserNameChoose);


        User user = (User) this.objects.get(position);
        txtUserNameChoose.setText(user.getUserName());
        if(user.getAvatar().equals("default")){
            civAvatarUserChoose.setImageResource(R.drawable.img_avatar_default);
        }else {
            Glide.with(context).load(user.getAvatar()).into(civAvatarUserChoose);
        }
//        radGroupChoose.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int i) {
//                switch (i){
//                    case R.id.radChoose:{
//                        break;
//                    }
//                }
//            }
//        });
        return rowItem;
    }
}
