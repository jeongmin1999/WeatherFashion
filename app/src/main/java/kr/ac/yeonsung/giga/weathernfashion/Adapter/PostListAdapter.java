package kr.ac.yeonsung.giga.weathernfashion.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import kr.ac.yeonsung.giga.weathernfashion.Activities.MainActivity;
import kr.ac.yeonsung.giga.weathernfashion.Fragment.PostFragment;
import kr.ac.yeonsung.giga.weathernfashion.Fragment.PostViewFragment;
import kr.ac.yeonsung.giga.weathernfashion.R;
import kr.ac.yeonsung.giga.weathernfashion.VO.PostList;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.ViewHolder>{

    private ArrayList<PostList> mData = null ;
    private Context context;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    StorageReference riversRef = storageRef.child("post");

    public PostListAdapter(Context context, ArrayList<PostList> mData) {
        this.mData = mData;
        this.context = context;
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView post_id ;
        ImageView post_image;


        public ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            post_id = itemView.findViewById(R.id.post_id);
            post_image = itemView.findViewById(R.id.post_image);

        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    public PostListAdapter(ArrayList<PostList> list) {
        mData = list ;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public PostListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder ;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(PostListAdapter.ViewHolder holder, int position) {
        String post_image_str = mData.get(position).getPost_image();
        String post_id = mData.get(position).getPost_id();

    riversRef.child(post_image_str).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
        @Override
        public void onSuccess(Uri uri) {
            Glide.with(context).load(uri)
                    .into(holder.post_image);
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception exception) {
        }
    });
        holder.post_id.setText(post_id);
        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle result = new Bundle();
                result.putString("id", post_id);
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