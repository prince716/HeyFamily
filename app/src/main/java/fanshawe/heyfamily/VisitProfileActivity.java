package fanshawe.heyfamily;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class VisitProfileActivity extends AppCompatActivity {

    String reciver_user_id;
    String sender_user_id;
    private Button sendFriendReq;
    private Button declineFriendReq;
    private TextView ProfileName;
    private TextView ProfileStatus;
    private ImageView ProfileImage;
    private DatabaseReference ref;
    private String CURRENT_STATE;
    private DatabaseReference frndReqRef;
    private FirebaseAuth mAuth;
    private DatabaseReference frndRef;
    private DatabaseReference notificationRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_profile);

        frndReqRef = FirebaseDatabase.getInstance().getReference().child("Friend_Request");
        mAuth = FirebaseAuth.getInstance();
        sender_user_id = mAuth.getCurrentUser().getUid();

        ref = FirebaseDatabase.getInstance().getReference().child("Users");
        frndRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
        notificationRef.keepSynced(true);

        reciver_user_id = getIntent().getExtras().get("visit_user_id").toString();

        sendFriendReq = findViewById(R.id.profile_visit_sendReq_btn);
        declineFriendReq = findViewById(R.id.profile_visit_decline_btn);
        ProfileName = findViewById(R.id.profile_visit_username);
        ProfileStatus = findViewById(R.id.profile_visit_status);
        ProfileImage = findViewById(R.id.profile_visit_userImage);

        CURRENT_STATE = "not_friends";

        ref.child(reciver_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("user_name").getValue().toString();
                String status = dataSnapshot.child("user_status").getValue().toString();
                String image = dataSnapshot.child("user_image").getValue().toString();

                ProfileName.setText(name);
                ProfileStatus.setText(status);
                Picasso.with(VisitProfileActivity.this).load(image).placeholder(R.drawable.default_profile).into(ProfileImage);

                frndReqRef.child(sender_user_id)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot.hasChild(reciver_user_id)) {
                                    String req_type = dataSnapshot.child(reciver_user_id).child("request_type").getValue().toString();

                                    if (req_type.equals("sent")) {
                                        CURRENT_STATE = "request_sent";
                                        sendFriendReq.setText("Cancel Friend Request");

                                        declineFriendReq.setVisibility(View.INVISIBLE);
                                        declineFriendReq.setEnabled(false);
                                    } else if (req_type.equals("received")) {
                                        CURRENT_STATE = "request_received";
                                        sendFriendReq.setText("Accept Friend Request");

                                        declineFriendReq.setVisibility(View.VISIBLE);
                                        declineFriendReq.setEnabled(true);

                                        declineFriendReq.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                DeclineFriendRequest();
                                            }
                                        });
                                    }
                                } else {
                                    frndRef.child(sender_user_id)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.hasChild(reciver_user_id)) {
                                                        CURRENT_STATE = "friends";
                                                        sendFriendReq.setText("Unfriend User");

                                                        declineFriendReq.setVisibility(View.INVISIBLE);
                                                        declineFriendReq.setEnabled(false);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        declineFriendReq.setVisibility(View.INVISIBLE);
        declineFriendReq.setEnabled(false);

        if (!sender_user_id.equals(reciver_user_id)) {
            sendFriendReq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendFriendReq.setEnabled(false);

                    if (CURRENT_STATE.equals("not_friends")) {
                        SendFriendRequest();
                    }
                    if (CURRENT_STATE.equals("request_sent")) {
                        CancelFriendRequest();
                    }
                    if (CURRENT_STATE.equals("request_received")) {
                        AcceptFriendRequest();
                    }
                    if (CURRENT_STATE.equals("friends")) {
                        UnfriendFriend();
                    }
                }
            });
        } else {
            declineFriendReq.setVisibility(View.INVISIBLE);
            sendFriendReq.setVisibility(View.INVISIBLE);
        }


    }

    private void DeclineFriendRequest() {
        frndReqRef.child(sender_user_id).child(reciver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            frndReqRef.child(reciver_user_id).child(sender_user_id).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sendFriendReq.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                sendFriendReq.setText("Send Friend Request");

                                                declineFriendReq.setVisibility(View.INVISIBLE);
                                                declineFriendReq.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void UnfriendFriend() {
        frndRef.child(sender_user_id).child(reciver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            frndRef.child(reciver_user_id).child(sender_user_id).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sendFriendReq.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                sendFriendReq.setText("Send Friend Request");

                                                declineFriendReq.setVisibility(View.INVISIBLE);
                                                declineFriendReq.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptFriendRequest() {
        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        final String saveCurrentDate = currentDate.format(callForDate.getTime());

        frndRef.child(sender_user_id).child(reciver_user_id).child("date").setValue(saveCurrentDate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        frndRef.child(reciver_user_id).child(sender_user_id).child("date").setValue(saveCurrentDate)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        frndReqRef.child(sender_user_id).child(reciver_user_id).removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            frndReqRef.child(reciver_user_id).child(sender_user_id).removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                sendFriendReq.setEnabled(true);
                                                                                CURRENT_STATE = "friends";
                                                                                sendFriendReq.setText("Unfriend User");

                                                                                declineFriendReq.setVisibility(View.INVISIBLE);
                                                                                sendFriendReq.setVisibility(View.INVISIBLE);
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }

    private void CancelFriendRequest() {
        frndReqRef.child(sender_user_id).child(reciver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            frndReqRef.child(reciver_user_id).child(sender_user_id).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sendFriendReq.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                sendFriendReq.setText("Send Friend Request");

                                                declineFriendReq.setVisibility(View.INVISIBLE);
                                                declineFriendReq.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void SendFriendRequest() {
        frndReqRef.child(sender_user_id).child(reciver_user_id).child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            frndReqRef.child(reciver_user_id).child(sender_user_id)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                HashMap<String, String> notificationData = new HashMap<String, String>();
                                                notificationData.put("from", sender_user_id);
                                                notificationData.put("type", "request");

                                                notificationRef.child(reciver_user_id).push().setValue(notificationData)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    sendFriendReq.setEnabled(true);
                                                                    CURRENT_STATE = "request_sent";
                                                                    sendFriendReq.setText("Cancel Friend Request");

                                                                    declineFriendReq.setVisibility(View.INVISIBLE);
                                                                    declineFriendReq.setEnabled(false);
                                                                }

                                                            }
                                                        });


                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}
