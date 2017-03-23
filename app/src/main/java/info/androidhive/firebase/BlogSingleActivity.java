package info.androidhive.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class BlogSingleActivity extends AppCompatActivity {
    private String post_key = null;
    private DatabaseReference databaseReference;
    private TextView blogSimpleTitle, blogSimpleDesc, blogSimpleUsername;
    private ImageView blogSimpleImage;
    private Button simpleRemovePost;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_single);
        blogSimpleDesc = (TextView) findViewById(R.id.simpleBlogDesc);
        blogSimpleTitle = (TextView) findViewById(R.id.simpleBlogTitle);
        blogSimpleImage = (ImageView) findViewById(R.id.simpleBlogImage);
        simpleRemovePost = (Button) findViewById(R.id.simpleRemovePost);
        final String post_key = getIntent().getExtras().getString("blog_id");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Blog");
        auth = FirebaseAuth.getInstance();
        //Toast.makeText(BlogSingleActivity.this,post_key,Toast.LENGTH_SHORT).show();
        databaseReference.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String post_title = (String) dataSnapshot.child("title").getValue();
                String post_desc = (String) dataSnapshot.child("desc").getValue();
                String post_image = (String) dataSnapshot.child("image").getValue();
                String post_uid = (String) dataSnapshot.child("uid").getValue();

                blogSimpleTitle.setText(post_title);
                blogSimpleDesc.setText(post_desc);
                Picasso.with(BlogSingleActivity.this).load(post_image).into(blogSimpleImage);
                //Toast.makeText(BlogSingleActivity.this,auth.getCurrentUser().getUid(),Toast.LENGTH_SHORT).show();
                if (auth.getCurrentUser().getUid().equals(post_uid)) {
                    simpleRemovePost.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        simpleRemovePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                databaseReference.child(post_key).removeValue();
                Intent mainIntent = new Intent(BlogSingleActivity.this, Web.class);
                startActivity(mainIntent);
                finish();
            }
        });

    }
}
