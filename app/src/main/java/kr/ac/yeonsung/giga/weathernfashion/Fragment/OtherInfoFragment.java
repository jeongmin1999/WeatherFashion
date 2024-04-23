package kr.ac.yeonsung.giga.weathernfashion.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;
import kr.ac.yeonsung.giga.weathernfashion.Activities.ChatActivity;
import kr.ac.yeonsung.giga.weathernfashion.Adapter.MyInfoAdapter;
import kr.ac.yeonsung.giga.weathernfashion.R;
import kr.ac.yeonsung.giga.weathernfashion.VO.MyInfoList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OtherInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OtherInfoFragment extends Fragment {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    StorageReference riversRef = storageRef.child("profile");
    RecyclerView.Adapter adapter;
    ArrayList<MyInfoList> list = new ArrayList();
    RecyclerView recyclerView;
    GridLayoutManager layoutManager;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    TextView othername, othercomment, back_pressed;
    String id;
    ImageView chat_image;
    CircleImageView otherprofile;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OtherInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OtherInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OtherInfoFragment newInstance(String param1, String param2) {
        OtherInfoFragment fragment = new OtherInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup)inflater.inflate(R.layout.fragment_other_info, container, false);
        Bundle bundle = getArguments();
        id = bundle.getString("id");
        System.out.println("아이디 : "+ id);
        othername = view.findViewById(R.id.other_name);
        othercomment = view.findViewById(R.id.other_comment);
        otherprofile = view.findViewById(R.id.other_profile);
        chat_image = view.findViewById(R.id.chat_image);
        recyclerView = view.findViewById(R.id.otherinfo_recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getActivity(),3);
        recyclerView.setLayoutManager(layoutManager);
        setProfile();
        getOtherPostList();
        chat_image.setOnClickListener(chatListener);
        // Inflate the layout for this fragment
        return view;
    }

    public void setProfile(){
        mDatabase.child("users").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println(snapshot.getValue());
                othername.setText(snapshot.child("user_name").getValue().toString());
                othercomment.setText(snapshot.child("user_comment").getValue().toString());
                String myprofile_str = snapshot.child("user_profile").getValue().toString();
                riversRef.child(myprofile_str).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getActivity()).load(uri)
                                .into(otherprofile);
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
    }
    public void getOtherPostList(){
        mDatabase.child("post").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    if (snapshot1.child("post_user_id").getValue().equals(id)) {
                        list.add(new MyInfoList(snapshot1.child("post_image").getValue().toString(), snapshot1.getKey()));
                    }
                }
                Collections.reverse(list);
                adapter = new MyInfoAdapter(getContext(),list);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    View.OnClickListener chatListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra("id",id);
            startActivity(intent);
        }
    };
}