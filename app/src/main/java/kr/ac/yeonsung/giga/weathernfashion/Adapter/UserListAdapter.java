package kr.ac.yeonsung.giga.weathernfashion.Adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import kr.ac.yeonsung.giga.weathernfashion.Activities.MainActivity;
import kr.ac.yeonsung.giga.weathernfashion.Fragment.OtherInfoFragment;
import kr.ac.yeonsung.giga.weathernfashion.Fragment.PostViewFragment;
import kr.ac.yeonsung.giga.weathernfashion.R;
import kr.ac.yeonsung.giga.weathernfashion.VO.UserList;

public class UserListAdapter extends ArrayAdapter<UserList> {
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    StorageReference riversRef = storageRef.child("profile");
    private List<UserList> list;
    String id_str;

    public UserListAdapter(@NonNull Context context, @NonNull List<UserList> user_list){
        super(context, 0, user_list);
        list = new ArrayList<>(user_list);

    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.user_list_item, parent, false
            );
        }

        TextView name = convertView.findViewById(R.id.user_name);
        TextView id = convertView.findViewById(R.id.user_id);
        CircleImageView image = convertView.findViewById(R.id.user_profile);
        LinearLayout ly = convertView.findViewById(R.id.ly);
        //getItem(position) 코드로 자동완성 될 아이템을 가져온다
        UserList userlist = getItem(position);

        if (userlist != null) {
            String name_str = userlist.getUser_name();
            id_str = userlist.getUser_id();
            String profile_str = userlist.getUser_profile();
            name.setText(name_str);
            id.setText(id_str);
            riversRef.child(profile_str).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(getContext()).load(uri)
                            .into(image);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
        }
        return convertView;
    }

    //-------------------------- 이 아래는 자동완성을 위한 검색어를 찾아주는 코드이다 --------------------------
    @NonNull
    @Override
    public Filter getFilter() {
        return userFilter;
    }

    private Filter userFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<UserList> suggestions = new ArrayList<>();
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(list);
            } else {
                String filterPattern = constraint.toString().trim();

                for (UserList item : list) {
                    if (item.getUser_name().contains(filterPattern)) {
                        suggestions.add(item);
                    }
                }
            }

            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((UserList) resultValue).getUser_name();
        }
    };
}
