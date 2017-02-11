package info.androidhive.firebase;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class MyTimeline extends AppCompatActivity {
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseUser;
    private DatabaseReference mDatabaseLike,mDatabaseDislike;
    private RecyclerView mBlogList;
    private boolean mProcessLike=false;
    private boolean mProcessDislike=false;
     public String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_timeline);
        mBlogList=(RecyclerView)findViewById(R.id.blog_list);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));
        auth = FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Blog");
        databaseUser=FirebaseDatabase.getInstance().getReference().child("User");
        mDatabaseLike=FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabaseDislike=FirebaseDatabase.getInstance().getReference().child("Dislikes");
        databaseUser.keepSynced(true);
        mDatabaseLike.keepSynced(true);
        mDatabaseDislike.keepSynced(true);
        databaseReference.keepSynced(true);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    @Override
    protected void onStart() {

        super.onStart();
        final FirebaseRecyclerAdapter<Blog,Web.BlogViewHolder> firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<Blog, Web.BlogViewHolder>(
                Blog.class,
                R.layout.blog_row,
                Web.BlogViewHolder.class,
                databaseReference
        ) {
            @Override
            protected void populateViewHolder(Web.BlogViewHolder viewHolder, Blog model, int position) {
                uid=model.getUid();
if(auth.getCurrentUser().getUid()==uid)
{
                final String post_key=getRef(position).getKey();
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getApplicationContext(),model.getImage());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setTime(model.getTime());
                viewHolder.setLikeButton(post_key);
                viewHolder.setDislikeButton(post_key);
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(Web.this,post_key,Toast.LENGTH_SHORT).show();
                        Intent simpleBlogIntent=new Intent(MyTimeline.this,BlogSingleActivity.class);
                        simpleBlogIntent.putExtra("blog_id",post_key);
                        startActivity(simpleBlogIntent);
                    }
                });
                viewHolder.likeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mProcessLike=true;

                        mDatabaseLike.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(mProcessLike){
                                    if (dataSnapshot.child(post_key).hasChild(auth.getCurrentUser().getUid())) {
                                        mDatabaseLike.child(post_key).child(auth.getCurrentUser().getUid()).removeValue();
                                        mProcessLike=false;

                                    } else {
                                        mDatabaseLike.child(post_key).child(auth.getCurrentUser().getUid()).setValue("RandomValue");
                                        mProcessLike=false;

                                    }
                                }}
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }


                });
                viewHolder.dislikeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mProcessDislike=true;

                        mDatabaseDislike.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(mProcessDislike){
                                    if (dataSnapshot.child(post_key).hasChild(auth.getCurrentUser().getUid())) {
                                        mDatabaseDislike.child(post_key).child(auth.getCurrentUser().getUid()).removeValue();
                                        mProcessDislike=false;

                                    } else {
                                        mDatabaseDislike.child(post_key).child(auth.getCurrentUser().getUid()).setValue("RandomValue");
                                        mProcessDislike=false;

                                    }
                                }}
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }


                });
            }}


        };
       /* if (auth.getCurrentUser().getUid()!=uid){
            mBlogList.removeOnItemTouchListener(RecyclerView.OnChildAttachStateChangeListener);
        }*/
        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }

    public  static class BlogViewHolder extends RecyclerView.ViewHolder{
        View mView;
        ImageButton likeButton,dislikeButton;
        DatabaseReference mDatabaseLike,mDatabaseDislike;

        FirebaseAuth mAuth;


        public BlogViewHolder(View itemView) {
            super(itemView);
            mView =itemView;
            likeButton=(ImageButton)mView.findViewById(R.id.like_button);
            dislikeButton=(ImageButton)mView.findViewById(R.id.dislike_button);
            mDatabaseLike=FirebaseDatabase.getInstance().getReference().child("Likes");
            mDatabaseDislike=FirebaseDatabase.getInstance().getReference().child("Dislikes");
            mAuth=FirebaseAuth.getInstance();
            mDatabaseLike.keepSynced(true);
            mDatabaseDislike.keepSynced(true);
        }

        public void setLikeButton (final String post_key)
        {

            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){
                        likeButton.setImageResource(R.mipmap.green_thumb_up);
                    }
                    else{
                        likeButton.setImageResource(R.mipmap.gray_thumb_up);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        public void setDislikeButton(final String post_key)
        {
            mDatabaseDislike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){
                        dislikeButton.setImageResource(R.mipmap.red_thumb_down);
                    }
                    else{
                        dislikeButton.setImageResource(R.mipmap.gray_thumb_down);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        public void setTitle(String title){
            TextView post_title=(TextView)mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }
        public void setTime(long time){
            TextView post_title=(TextView)mView.findViewById(R.id.post_time);
            post_title.setText((int) time);
        }
        public void setDesc(String desc){
            TextView post_desc=(TextView)mView.findViewById(R.id.post_desc);
            post_desc.setText(desc);
        }
        public void setUsername(String username)
        {
            TextView post_desc=(TextView)mView.findViewById(R.id.post_username);
            post_desc.setText(username);
        }

        public void setImage(final Context ctx, final String Image){
            final ImageView post_image=(ImageView)mView.findViewById(R.id. postImage);
//            Picasso.with(ctx).load(Image).into(post_image);
            Picasso.with(ctx).load(Image).networkPolicy(NetworkPolicy.OFFLINE).into(post_image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(Image).into(post_image);

                }
            });

        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id=item.getItemId();
        switch (id)
        {
            case android.R.id.home:
                //Write your logic here
                startActivity(new Intent(MyTimeline.this,Web.class));
                this.finish();

                return true;


        }
        return super.onOptionsItemSelected(item);
    }

}