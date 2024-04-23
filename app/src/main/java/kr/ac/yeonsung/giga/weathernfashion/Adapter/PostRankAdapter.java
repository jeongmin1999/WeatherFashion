package kr.ac.yeonsung.giga.weathernfashion.Adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import kr.ac.yeonsung.giga.weathernfashion.Activities.MainActivity;
import kr.ac.yeonsung.giga.weathernfashion.Fragment.HomeFragment;
import kr.ac.yeonsung.giga.weathernfashion.Fragment.PostViewFragment;
import kr.ac.yeonsung.giga.weathernfashion.R;
import kr.ac.yeonsung.giga.weathernfashion.VO.PostRank;
import kr.ac.yeonsung.giga.weathernfashion.methods.API;

public class PostRankAdapter extends RecyclerView.Adapter<PostRankAdapter.ViewHolder> {

    private ArrayList<PostRank> mData = null ;

    HomeFragment homeFragment = new HomeFragment();
    private Context context;
    private Activity activity;
    MainActivity mainActivity;
    String mintemp;
    String maxtemp;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    StorageReference riversRef = storageRef.child("post");
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    HashMap<String,Boolean> hash = new HashMap<>();
    API api = new API();
    public PostRankAdapter(Context context, ArrayList<PostRank> mData) {
        this.mData = mData;
        this.context = context;
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView rank,max,min, likecount, post_id;
        ImageView like_home;
        ImageView rank_icon;
        TextView mint;
        TextView maxt;
        public ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            rank = itemView.findViewById(R.id.rank);
            imageView = itemView.findViewById(R.id.imageView);
            rank_icon = itemView.findViewById(R.id.rank_icon);
            max = itemView.findViewById(R.id.max_temp_home);
            min = itemView.findViewById(R.id.min_temp_home);
            likecount = itemView.findViewById(R.id.likecount_home);
            post_id = itemView.findViewById(R.id.post_id);
            like_home = itemView.findViewById(R.id.like_home);
            mint = itemView.findViewById(R.id.mint);
            maxt = itemView.findViewById(R.id.maxt);
        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    public PostRankAdapter(ArrayList<PostRank> list) {
        mData = list ;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_rank_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder ;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String image_str = mData.get(position).getImage();
        String post_max_home = mData.get(position).getMax_temp();
        String post_min_home = mData.get(position).getMin_temp();
        String likecount_str = mData.get(position).getLike();
        String post_id_str = mData.get(position).getPost_id();

        holder.rank.setText(String.valueOf(position+1));
        holder.max.setText(post_max_home);
        holder.min.setText(post_min_home);
        holder.post_id.setText(post_id_str);
        holder.likecount.setText(likecount_str);

        riversRef.child(image_str).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri)
                        .into(holder.imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });


        if(holder.rank.getText().equals("1")) {
            holder.rank_icon.setImageResource(R.drawable.rank1);
            System.out.println(holder.rank.getText());
        }
        if(holder.rank.getText().equals("2")) {
            holder.rank_icon.setImageResource(R.drawable.rank2);
            System.out.println(holder.rank.getText());
        }
        if(holder.rank.getText().equals("3")) {
            holder.rank_icon.setImageResource(R.drawable.rank3);
            System.out.println(holder.rank.getText());
        } else{
        }



        mDatabase.child("post").child(post_id_str).child("post_likes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null){
                    hash.clear();
                    hash = (HashMap<String, Boolean>) snapshot.getValue();

                    if (hash.containsKey(user.getUid())) {
                        if (hash.get(user.getUid()) == true) {
                            holder.like_home.setImageResource(R.drawable.ic_baseline_favorite_24);
                        } else {
                            holder.like_home.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                        }
                    } else {
                        holder.like_home.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                    }
                }else{
                    holder.like_home.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle result = new Bundle();
                result.putString("id", post_id_str);
                FragmentManager fm = ((MainActivity)context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction;
                PostViewFragment postViewFragment = new PostViewFragment();
                postViewFragment.setArguments(result);
                fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.addToBackStack(null)
                        .setCustomAnimations(R.anim.fade_in,R.anim.fade_out)
                        .replace(R.id.main_ly,postViewFragment)
                        .commit();
            }
        });
    }
    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size() ;
    }
}