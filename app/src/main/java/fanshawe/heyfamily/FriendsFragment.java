package fanshawe.heyfamily;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {


    private RecyclerView allUsersList;
    private DatabaseReference allDatabaseUserRef;
    private View frndsView;
    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        frndsView = inflater.inflate(R.layout.fragment_friends, container, false);


        allUsersList = frndsView.findViewById(R.id.all_users_list);
        allUsersList.setHasFixedSize(true);
        allUsersList.setLayoutManager(new LinearLayoutManager(getContext()));
        allDatabaseUserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        return frndsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<FriendsClass, AllUsersViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<FriendsClass, AllUsersViewHolder>
                (
                        FriendsClass.class,
                        R.layout.friends_display_layout,
                        AllUsersViewHolder.class,
                        allDatabaseUserRef
                ) {
            @Override
            protected void populateViewHolder(AllUsersViewHolder viewHolder, FriendsClass model, int position) {
                viewHolder.setUser_name(model.getUser_name());
                viewHolder.setUser_status(model.getUser_status());
                viewHolder.setUser_image(getContext().getApplicationContext(), model.getUser_image());
            }
        };

        allUsersList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class AllUsersViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public AllUsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setUser_name(String user_name) {
            TextView name = mView.findViewById(R.id.all_users_username);
            name.setText(user_name);
        }

        public void setUser_status(String user_status) {
            TextView status = mView.findViewById(R.id.all_user_status);
            status.setText(user_status);
        }

        public void setUser_image(Context ctx, String user_image) {
            CircleImageView image = mView.findViewById(R.id.all_users_profile_image);
            Picasso.with(ctx).load(user_image).into(image);
        }
    }
}
