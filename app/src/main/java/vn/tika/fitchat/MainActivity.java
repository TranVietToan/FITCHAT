package vn.tika.fitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.tika.fitchat.Fragment.GroupChatFragment;
import vn.tika.fitchat.Fragment.ProfileFragment;
import vn.tika.fitchat.Fragment.UserChatFragment;
import vn.tika.fitchat.Model.User;

public class MainActivity extends AppCompatActivity {
    CircleImageView civUserAvatarMain;
    TextView txtUserNameMain;

    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addControls();
        addEvents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout: {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;
            }
            case R.id.createGroup:{
                startCreateGroup();
                return true;
            }
        }

        return false;
    }

    private void addControls() {
        civUserAvatarMain = findViewById(R.id.civUserAvatarMain);
        txtUserNameMain = findViewById(R.id.txtUserNameMain);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        bottomNavigationView = findViewById(R.id.bottom_nav_view);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
    }

    private void addEvents() {
        loadFragment(new UserChatFragment());
        eventBottomNavOnClick();
        getDataUserMain();
    }

    private void eventBottomNavOnClick() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()){
                    case R.id.item_message:{
                        fragment = new UserChatFragment();
                        loadFragment(fragment);
                        toolbar.setVisibility(View.VISIBLE);
                        return true;
                    }
                    case R.id.item_profile:{
                        fragment = new ProfileFragment();
                        loadFragment(fragment);
                        toolbar.setVisibility(View.GONE);
                        return true;
                    }
                    case R.id.action_group:{
                        fragment = new GroupChatFragment();
                        loadFragment(fragment);
                        toolbar.setVisibility(View.VISIBLE);
                        return true;
                    }
                    default:{
                        return false;
                    }
                }
            }
        });
    }

    public void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_main, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    //lay du lieu thong tin nguioi dung dang dang nhap
    private void getDataUserMain() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                txtUserNameMain.setText(user.getUserName());
                if(user.getAvatar().equals("default")){
                    civUserAvatarMain.setImageResource(R.drawable.img_avatar_default);
                }else {
                    Glide.with(getApplicationContext()).load(user.getAvatar()).into(civUserAvatarMain);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void startCreateGroup() {
        startActivity(new Intent(MainActivity.this, CreateGroupActivity.class));
    }


    // set trang thai hoat dong
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
        setActiveStatus("offline");
    }
}