package kr.ac.yeonsung.giga.weathernfashion.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import kr.ac.yeonsung.giga.weathernfashion.Activities.MainActivity;
import kr.ac.yeonsung.giga.weathernfashion.Adapter.PostListAdapter;
import kr.ac.yeonsung.giga.weathernfashion.Adapter.UserListAdapter;
import kr.ac.yeonsung.giga.weathernfashion.R;
import kr.ac.yeonsung.giga.weathernfashion.VO.PostList;
import kr.ac.yeonsung.giga.weathernfashion.VO.UserList;
import kr.ac.yeonsung.giga.weathernfashion.methods.API;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostFragment extends Fragment {

    RecyclerView.Adapter adapter;
    ArrayList<PostList> list = new ArrayList();
    RecyclerView recyclerView;
    GridLayoutManager layoutManager;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    ArrayList<String> min_list = new ArrayList<>();
    ArrayList<String> max_list = new ArrayList<>();
    ArrayList<String> cate_list = new ArrayList<>(Arrays.asList("전체","캐쥬얼" ,"아메카지", "미니멀", "스트릿","기타"));
    ArrayList<String> gender_list = new ArrayList<>(Arrays.asList("전체", "남자", "여자"));
    Long minL;
    Long maxL;
    Long tempL;
    Spinner min_spinner, max_spinner, cate_spinner, gender_spinner;
    AutoCompleteTextView autoCompleteTextView;
    ArrayList<UserList> user_list = new ArrayList<>();
    String name;
    API api = new API();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BoardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostFragment newInstance(String param1, String param2) {
        PostFragment fragment = new PostFragment();
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
        View view = inflater.inflate(R.layout.fragment_post, container, false);


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        for(int i=-20; i<40; i++){
            min_list.add(String.valueOf(i));
            max_list.add(String.valueOf(i));
        }

        user_name_set();
        recyclerView = view.findViewById(R.id.grid_recyclerView);
        min_spinner = view.findViewById(R.id.min);
        max_spinner = view.findViewById(R.id.max);
        cate_spinner = view.findViewById(R.id.ca);
        gender_spinner = view.findViewById(R.id.gender_spinner);
        setSpinner();
        min_spinner.setOnItemSelectedListener(spinnerListener);
        max_spinner.setOnItemSelectedListener(spinnerListener);
        cate_spinner.setOnItemSelectedListener(spinnerListener);
        gender_spinner.setOnItemSelectedListener(spinnerListener);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getActivity(),3);
        recyclerView.setLayoutManager(layoutManager);
        autoCompleteTextView = view.findViewById(R.id.autoDatas);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<UserList> l =  new ArrayList<>();
                l.add((UserList) parent.getItemAtPosition(position));

                Bundle result = new Bundle();
                result.putString("id", l.get(0).getUser_id());
                FragmentManager fm = ((MainActivity)getContext()).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction;
                OtherInfoFragment otherInfoFragment = new OtherInfoFragment();
                otherInfoFragment.setArguments(result);
                fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.addToBackStack(null)
                        .setCustomAnimations(R.anim.fade_in,R.anim.fade_out)
                        .replace(R.id.main_ly,otherInfoFragment)
                        .commit();
            }
        });
//        getPostList();



    }

    public void user_name_set(){
        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    String name_str = snapshot1.child("user_name").getValue().toString();
                    String id_str = snapshot1.getKey();
                    String profile_str = snapshot1.child("user_profile").getValue().toString();
                    user_list.add(new UserList(id_str,name_str,profile_str));
                    UserListAdapter userListAdapter = new UserListAdapter(getActivity(), user_list);
                    autoCompleteTextView.setAdapter(userListAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getPostList(){

        mDatabase.child("post").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    minL = Long.parseLong(min_spinner.getSelectedItem().toString());
                    maxL = Long.parseLong(max_spinner.getSelectedItem().toString());
                    tempL = Long.parseLong(snapshot1.child("post_temp").getValue().toString());
                    if(minL<=tempL && tempL<=maxL) {
                        if(gender_spinner.getSelectedItem().equals("전체")){
                            if(cate_spinner.getSelectedItem().equals("전체")){
                                list.add(new PostList(snapshot1.child("post_image").getValue().toString(), snapshot1.getKey()));
                            } else{
                                ArrayList<String> post_cate = new ArrayList<>();
                                post_cate = (ArrayList<String>)snapshot1.child("post_categories").getValue();
                                if(post_cate.contains(cate_spinner.getSelectedItem().toString())) {
                                    list.add(new PostList(snapshot1.child("post_image").getValue().toString(), snapshot1.getKey()));
                                }
                            }
                        } else if(gender_spinner.getSelectedItem().equals("남자")) {
                            if (cate_spinner.getSelectedItem().equals("전체")) {
                                    if (snapshot1.child("post_gender").getValue().equals("남자")) {
                                        list.add(new PostList(snapshot1.child("post_image").getValue().toString(), snapshot1.getKey()));
                                    }
                                } else{
                                    ArrayList<String> post_cate = new ArrayList<>();
                                    post_cate = (ArrayList<String>) snapshot1.child("post_categories").getValue();
                                    if (post_cate.contains(cate_spinner.getSelectedItem().toString())&& snapshot1.child("post_gender").getValue().equals("남자")) {
                                        list.add(new PostList(snapshot1.child("post_image").getValue().toString(), snapshot1.getKey()));
                                    }
                                }
                        } else if(gender_spinner.getSelectedItem().equals("여자")) {
                            if (cate_spinner.getSelectedItem().equals("전체")) {
                                if (snapshot1.child("post_gender").getValue().equals("여자")) {
                                    System.out.println("여자");
                                    list.add(new PostList(snapshot1.child("post_image").getValue().toString(), snapshot1.getKey()));
                                }
                            } else {
                                ArrayList<String> post_cate = new ArrayList<>();
                                post_cate = (ArrayList<String>) snapshot1.child("post_categories").getValue();
                                if (post_cate.contains(cate_spinner.getSelectedItem().toString()) && snapshot1.child("post_gender").getValue().equals("여자")) {
                                    list.add(new PostList(snapshot1.child("post_image").getValue().toString(), snapshot1.getKey()));
                                }
                            }
                        }
                    }
                }
                Collections.reverse(list);
                adapter = new PostListAdapter(getContext(),list);
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setSpinner(){
        ArrayAdapter<String> min_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,min_list);
        min_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        min_spinner.setAdapter(min_adapter);

        ArrayAdapter<String> max_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,max_list);
        min_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        max_spinner.setAdapter(max_adapter);
        max_spinner.setSelection(max_list.size()-1);

        ArrayAdapter<String> ca_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,cate_list);
        ca_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cate_spinner.setAdapter(ca_adapter);

        ArrayAdapter<String> gender_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,gender_list);
        gender_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender_spinner.setAdapter(gender_adapter);
    }

    AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            getPostList();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };
}