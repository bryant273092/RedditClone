package com.example.reddit;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView post_list_view;
    private List<RedditPost> post_list;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private PostRecyclerAdapter postRecyclerAdapter;
    private DocumentSnapshot lastVisible;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        post_list = new ArrayList<>();
        post_list_view = view.findViewById(R.id.post_list_view);
        firebaseAuth = FirebaseAuth.getInstance();
        postRecyclerAdapter = new PostRecyclerAdapter(post_list);
        post_list_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        post_list_view.setAdapter(postRecyclerAdapter);
        if(firebaseAuth.getCurrentUser()!= null) {

            firebaseFirestore = FirebaseFirestore.getInstance();

            post_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(-1);

                    if(reachedBottom){

                        String desc = lastVisible.getString("desc");
                        Toast.makeText(container.getContext(), "Reached :" + desc, Toast.LENGTH_SHORT).show();
                        loadmorePost();
                    }
                }
            });
            Query firstQuery = firebaseFirestore.collection("Posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(3);
            firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size()-1);

                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String blogPostID = doc.getDocument().getId();
                            RedditPost redditPost = doc.getDocument().toObject(RedditPost.class).withId(blogPostID);
                            post_list.add(redditPost);

                            postRecyclerAdapter.notifyDataSetChanged();

                        }
                    }

                }
            });
        }
        // Inflate the layout for this fragment
        return view;

    }
    public void loadmorePost(){
        Query nextQuery = firebaseFirestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING).limit(3).startAfter(lastVisible);
        nextQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(!queryDocumentSnapshots.isEmpty()) {


                    lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String blogPostID = doc.getDocument().getId();
                            RedditPost redditPost = doc.getDocument().toObject(RedditPost.class).withId(blogPostID);
                            post_list.add(redditPost);

                            postRecyclerAdapter.notifyDataSetChanged();

                        }
                    }
                }

            }
        });

    }

}
