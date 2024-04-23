package kr.ac.yeonsung.giga.weathernfashion.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import kr.ac.yeonsung.giga.weathernfashion.R;
import kr.ac.yeonsung.giga.weathernfashion.VO.TempReply;
import kr.ac.yeonsung.giga.weathernfashion.methods.PostMethods;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{
    private Context context;
    private ArrayList<TempReply> mData = null;
    private HashMap<String, Boolean> hash = new HashMap<>();
    private CommentAdapter commentAdapter;
    private boolean state = false;
    private boolean mode = false;
    private RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
    private String user_id, post_id, content, user_name, root_id, parent_id;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    StorageReference riversRef = storageRef.child("profile");

    public CommentAdapter(Context context, ArrayList<TempReply> mData) {
        this.mData = mData;
        this.context = context;
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView user_profile;
        private TextView user_name, reply_text, reply_time,user_id, reply_like_count, comment_count;
        private RecyclerView comment_view;
        private ImageView reply_like, reply_comment, comment_btn, comment_level;
        private EditText comment_edit;

        public ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            user_profile = itemView.findViewById(R.id.user_profile);
            user_name = itemView.findViewById(R.id.user_name);
            reply_text = itemView.findViewById(R.id.reply_text);
            reply_time = itemView.findViewById(R.id.reply_time);
            user_id = itemView.findViewById(R.id.user_id);
            reply_like = itemView.findViewById(R.id.reply_like);

            reply_like_count = itemView.findViewById(R.id.reply_like_count);

            comment_level = itemView.findViewById(R.id.comment_level);

        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    public CommentAdapter(ArrayList<TempReply> list) {
        mData = list ;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder ;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(CommentAdapter.ViewHolder holder, int position) {
        int index = position;
        holder.user_name.setText(mData.get(index).getName());
        holder.reply_text.setText(mData.get(index).getContent());
        holder.reply_time.setText(mData.get(index).getTime());
        holder.user_id.setText(mData.get(index).getUser_id());

        holder.reply_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(mData.get(index).getPost_id() + " " + mData.get(index).getReply_id());
                onLikeClicked(mDatabase.child("TempReply").child(mData.get(index).getPost_id())
                        .child(mData.get(index).getReply_id()));
            }
        });



        mDatabase.child("TempReply").child(mData.get(index).getPost_id())
                .child(mData.get(index).getReply_id()).child("reply_likeCount").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        holder.reply_like_count.setText(snapshot.getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        System.out.println(mData.get(index).getPost_id() + " " +mData.get(index).getReply_id());

        mDatabase.child("TempReply").child(mData.get(index).getPost_id())
                .child(mData.get(index).getReply_id()).child("likeList").addValueEventListener(
                        new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println(snapshot.getValue());
                if (snapshot.getValue() != null){
                    hash.clear();
                    hash = (HashMap<String, Boolean>) snapshot.getValue();
                    System.out.println("좋아요 누른 사람들 :" + hash);
                    System.out.println("좋아요 누른 사람들 :" + hash.containsKey(mAuth.getCurrentUser().getUid()));
                    if (hash.containsKey(mAuth.getCurrentUser().getUid())) {
                        System.out.println("해쉬에 내 유저 아이디가 있으면" + hash.containsKey(mAuth.getCurrentUser().getUid()));
                        if (hash.get(mAuth.getCurrentUser().getUid()) == true) {
                            holder.reply_like.setImageResource(R.drawable.ic_baseline_favorite_24);
                        } else {
                            holder.reply_like.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                        }
                    } else {
                        holder.reply_like.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                    }
                }else{
                    holder.reply_like.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String user_profile_str;

                user_profile_str = snapshot.child("user_profile").getValue().toString();
                riversRef.child(user_profile_str).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(context).load(uri)
                                .into(holder.user_profile);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        System.out.println(post_id);
//        mDatabase.child("TempReply").child(post_id).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                ArrayList<TempReply> commentList = new ArrayList<>();
//                commentList.clear();
//
//                for (DataSnapshot snapshot1:snapshot.getChildren()) {
//                    if (snapshot1.child("mode").getValue().toString() == "true"
//                            && snapshot1.child("parent").getValue().toString().equals(mData.get(index).getReply_id())
//
//                    ){
//                        String postId = snapshot.getKey();
//                        String conTent = snapshot1.child("content").getValue().toString();
//                        String userId = snapshot1.child("user_id").getValue().toString();
//                        String time = snapshot1.child("time").getValue().toString();
//                        String userName = snapshot1.child("name").getValue().toString();
//                        String replyId = snapshot1.getKey();
//                        TempReply tempReply = new TempReply(postId, conTent, userId, time, userName, replyId);
//                        commentList.add(tempReply);
//                    }
//                }
//                commentAdapter = new CommentAdapter(context, commentList);
//                holder.comment_view.setAdapter(commentAdapter);
//                commentAdapter.notifyDataSetChanged();
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

    }
    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size() ;
    }

    private void onLikeClicked(DatabaseReference replyRef) {
        replyRef.runTransaction(new Transaction.Handler() {

            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                TempReply tempReply = mutableData.getValue(TempReply.class);
                if (tempReply == null) {
                    return Transaction.success(mutableData);
                }

                //좋아요 누른 사람을 확인
                if (tempReply.getLikeList().containsKey(mAuth.getCurrentUser().getUid()))
                {
                    // Unstar the post and remove self from stars
                    //좋아요 취소
                    tempReply.setReply_likeCount(tempReply.getReply_likeCount() - 1);
                    tempReply.getLikeList().remove(mAuth.getCurrentUser().getUid());
                } else {
                    // Star the post and add self to stars
                    //좋아요 증가
                    tempReply.setReply_likeCount(tempReply.getReply_likeCount() + 1);
                    tempReply.getLikeList().put(mAuth.getCurrentUser().getUid(), true);
                }

                // Set value and report transaction success
                System.out.println(tempReply);
                mutableData.setValue(tempReply);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   DataSnapshot currentData) {
                // Transaction completed
            }
        });
    }
}