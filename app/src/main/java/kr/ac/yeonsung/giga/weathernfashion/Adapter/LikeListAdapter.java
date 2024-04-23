package kr.ac.yeonsung.giga.weathernfashion.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import kr.ac.yeonsung.giga.weathernfashion.Activities.ChatActivity;
import kr.ac.yeonsung.giga.weathernfashion.Activities.MainActivity;
import kr.ac.yeonsung.giga.weathernfashion.Fragment.ChatListFragment;
import kr.ac.yeonsung.giga.weathernfashion.Fragment.MyInfoFragment;
import kr.ac.yeonsung.giga.weathernfashion.Fragment.OtherInfoFragment;
import kr.ac.yeonsung.giga.weathernfashion.Fragment.PostViewFragment;
import kr.ac.yeonsung.giga.weathernfashion.R;

public class LikeListAdapter extends RecyclerView.Adapter<LikeListAdapter.ViewHolder>{

    private ArrayList<PostViewFragment.LikeList> mData = null ;
    private Context context;
    DatabaseReference mDatabase;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    StorageReference riversRef = storageRef.child("profile");

    public LikeListAdapter(Context context, ArrayList<PostViewFragment.LikeList> mData) {
        this.mData = mData;
        this.context = context;
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView user_name;
        TextView user_id;
        CircleImageView user_profile;


        public ViewHolder(View itemView) {
            super(itemView) ;

            user_name = itemView.findViewById(R.id.like_name);
            user_id = itemView.findViewById(R.id.like_id);
            user_profile = itemView.findViewById(R.id.like_profile);

        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    public LikeListAdapter(ArrayList<PostViewFragment.LikeList> list) {
        mData = list ;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.like_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder ;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mDatabase = FirebaseDatabase.getInstance().getReference();

        String user_id_str = mData.get(position).getUser_id();
        String user_name_str = mData.get(position).getUser_name();
        String user_profile_str = mData.get(position).getUser_profile();

        holder.user_id.setText(user_id_str);
        holder.user_name.setText(user_name_str);

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

        holder.user_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_id = user_id_str;
                if(!user_id.equals(user.getUid())) {
                    Bundle result = new Bundle();
                    result.putString("id", user_id);
                    FragmentManager fm = ((MainActivity) context).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction;
                    OtherInfoFragment otherInfoFragment = new OtherInfoFragment();
                    otherInfoFragment.setArguments(result);
                    fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.addToBackStack(null)
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .replace(R.id.main_ly, otherInfoFragment)
                            .commit();

                }else {
                    Bundle result = new Bundle();
                    result.putString("id", user_id);
                    FragmentManager fm = ((MainActivity) context).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction;
                    MyInfoFragment myInfoFragment = new MyInfoFragment();
                    myInfoFragment.setArguments(result);
                    fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.addToBackStack(null)
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .replace(R.id.main_ly, myInfoFragment)
                            .commit();
                }
            }
        });



    }
    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size() ;
    }

}