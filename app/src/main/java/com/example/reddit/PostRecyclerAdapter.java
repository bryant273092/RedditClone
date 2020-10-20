package com.example.reddit;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class PostRecyclerAdapter extends RecyclerView.Adapter<PostRecyclerAdapter.ViewHolder> {

    public List<RedditPost> redditPostList;

    public Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private TextView postUserName;



    public PostRecyclerAdapter(List<RedditPost> redditPostList){

        this.redditPostList = redditPostList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_list_item, viewGroup,false);
        context = viewGroup.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        viewHolder.setIsRecyclable(false);

        final String blogPostId = redditPostList.get(i).BlogPostId;

        final String currentUserId = firebaseAuth.getCurrentUser().getUid();

        String desc_data = redditPostList.get(i).getDesc();
        viewHolder.setDescText(desc_data);

        String user_id = redditPostList.get(i).getUser_id();

        //data here
        firebaseFirestore.collection("USERS").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    String userName = task.getResult().getString("name");
                    viewHolder.setUserData(userName);

                }else{

                }

            }
        });
        try {
            long millisecond = redditPostList.get(i).getTimestamp().getTime();
            String dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
            viewHolder.setTime(dateString);
        } catch (Exception e) {

            Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }

        firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(!queryDocumentSnapshots.isEmpty()){

                    int count = queryDocumentSnapshots.size();
                    viewHolder.updateLikesCount(count);


                }else{
                    viewHolder.updateLikesCount(0);

                }

            }
        });


        firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if(documentSnapshot.exists()){
                    viewHolder.postLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.a_search));
                }else {


                    viewHolder.postLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.heart_logo));
                }
            }

        });


        viewHolder.postLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (!task.getResult().exists()){

                            Map<String,Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(currentUserId).set(likesMap);
                        }else{
                            firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(currentUserId).delete();
                        }

                    }
                });

            }
        });


    }



    @Override
    public int getItemCount() {
        return redditPostList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private View mView;
        private TextView postDate;

        private TextView descView;
        private ImageView postLikeBtn;
        private TextView postLikeCount;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            postLikeBtn = mView.findViewById(R.id.postLikeBtn);
        }
        public void setDescText(String text){
            descView = mView.findViewById(R.id.post_description);
            descView.setText(text);
        }
        public void setTime(String date){

            postDate = mView.findViewById(R.id.post_date);
            postDate.setText(date);

        }

        public void setUserData(String name){

            postUserName = mView.findViewById(R.id.user_name);

            postUserName.setText(name);





        }
        public void updateLikesCount(int count){

            postLikeCount = mView.findViewById(R.id.post_like_count);
            postLikeCount.setText(count + " Likes");

        }
    }
}
