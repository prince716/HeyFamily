package fanshawe.heyfamily;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserFriendsActivity extends AppCompatActivity {

    String online_user_id;
    private RecyclerView myfrndList;
    private DatabaseReference frndsRef;
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_friends);
        mAuth = FirebaseAuth.getInstance();

        mToolbar = findViewById(R.id.userFrnds_app_bar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);


        online_user_id = mAuth.getCurrentUser().getUid();
        frndsRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        frndsRef.keepSynced(true);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userRef.keepSynced(true);
        myfrndList = findViewById(R.id.friends_list);
        myfrndList.setHasFixedSize(true);
        myfrndList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<UserFriends, FriendsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<UserFriends, FriendsViewHolder>
                (
                        UserFriends.class,
                        R.layout.friends_display_layout,
                        FriendsViewHolder.class,
                        frndsRef
                ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder viewHolder, UserFriends model, int position) {
                viewHolder.setDate(model.getDate());
                final String list_user_id = getRef(position).getKey();

                userRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String userName = dataSnapshot.child("user_name").getValue().toString();
                        String userImage = dataSnapshot.child("user_image").getValue().toString();

                        FriendsViewHolder.setUserName(userName);
                        FriendsViewHolder.setUserImage(getApplicationContext(), userImage);

                        FriendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]
                                        {
                                                userName + "'s Profile",
                                                "Send a Message"
                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(UserFriendsActivity.this);
                                builder.setTitle("Select Options");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int position) {
                                        if (position == 0) {
                                            Intent profileIntent = new Intent(UserFriendsActivity.this, VisitProfileActivity.class);
                                            profileIntent.putExtra("visit_user_id", list_user_id);
                                            startActivity(profileIntent);
                                        }
                                        if (position == 1) {
                                            Intent chatIntent = new Intent(UserFriendsActivity.this, ChatActivity.class);
                                            chatIntent.putExtra("visit_user_id", list_user_id);
                                            chatIntent.putExtra("user_name", userName);
                                            startActivity(chatIntent);
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        myfrndList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {
        static View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public static void setUserName(String userName) {
            TextView userNameDisplay = mView.findViewById(R.id.all_users_username);
            userNameDisplay.setText(userName);
        }

        public static void setUserImage(final Context ctx, final String userImage) {
            CircleImageView image = mView.findViewById(R.id.all_users_profile_image);
            Picasso.with(ctx).load(userImage).into(image);
        }

        public void setDate(String date) {
            TextView sinceFriendsDate = mView.findViewById(R.id.all_user_status);
            sinceFriendsDate.setText(date);
        }
    }
}
