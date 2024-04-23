package kr.ac.yeonsung.giga.weathernfashion.Activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Array;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import kr.ac.yeonsung.giga.weathernfashion.R;
import kr.ac.yeonsung.giga.weathernfashion.VO.Categories;
import kr.ac.yeonsung.giga.weathernfashion.VO.Post;
import kr.ac.yeonsung.giga.weathernfashion.methods.API;
import kr.ac.yeonsung.giga.weathernfashion.methods.PostMethods;

public class PostActivity extends AppCompatActivity {
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private DatabaseReference mDatabase;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private boolean valid = false;
    private Float lat =0.1f , lon = 1.2f;
    Geocoder g;
    private Uri imageUri = null;
    StorageReference storageRef = storage.getReference();
    API api = new API();
    PostMethods postMethods = new PostMethods();
    TextView post_done,temp,postlocation,postdate,choice_post_categotis
            ,choice_post_categotis2,post_selected_category,mintemp,maxtemp;
    EditText post_main_text;
    ImageView post_img;
    List<String> mSelectedItems;
    AlertDialog.Builder builder;
    String final_selection;
    String attrLATITUDE;
    String attrLATITUDE_REF;
    String attrLONGITUDE;
    String attrLONGITUDE_REF;
    String attrDate;
    String user_name;
    String user_gender;
    ImageView imageView2;
    String image_uri = null;
    String image_str = null;
    ArrayList<String> post_categories =new ArrayList<>();
    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        g = new Geocoder(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        setContentView(R.layout.activity_post);
        post_done = findViewById(R.id.post_done);
        post_main_text = findViewById(R.id.post_main_text);
        mintemp = findViewById(R.id.image_mintemp);
        maxtemp = findViewById(R.id.image_maxtemp);
        temp = findViewById(R.id.image_temp);
        postlocation = findViewById(R.id.image_location);

        postdate = findViewById(R.id.photo_weather3);
        choice_post_categotis = findViewById(R.id.choice_post_categoris);
        choice_post_categotis2 = findViewById(R.id.choice_post_categoris2);
        post_selected_category = findViewById(R.id.post_selected_category);
        imageView2 = findViewById(R.id.imageView2);
        post_img = findViewById(R.id.post_img);

        //리스너 등록
//        back_to_post_main.setOnClickListener(back_to);
        post_img.setOnClickListener(get_post_img);
//        choice_post_categotis.setOnClickListener(choice_post_categoty);
        choice_post_categotis.setOnClickListener(btn_listener);
        getName();
        post_done.setOnClickListener(btn_listener);



//        UploadTask uploadTask = mountainsRef.putBytes(data);
//        uploadTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle unsuccessful uploads
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
//                // ...
//            }
//        });

    }
    View.OnClickListener btn_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.post_img:
                    Intent galleryIntent = new Intent();
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/");
                    activityResult.launch(galleryIntent);
                    break;
                case R.id.choice_post_categoris:
                    showDialog();
                    break;
                case R.id.post_done:
                    PostImage();
                    setPost();
                    break;
            }
        }
    };



    public void showDialog(){
        mSelectedItems = new ArrayList<>();
        builder = new AlertDialog.Builder(PostActivity.this);
        builder.setTitle(" 카테고리 선택");
        builder.setMultiChoiceItems(R.array.categoris, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                String[] items = getResources().getStringArray(R.array.categoris);
                if(isChecked){
                    mSelectedItems.add(items[which]);
                }else if(mSelectedItems.contains(items[which])){
                    mSelectedItems.remove(items[which]);
                }
            }
        });

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final_selection = "";
                for(String item : mSelectedItems){
                    final_selection = final_selection +" "+item;
                }
                Toast.makeText(getApplicationContext()," 선택 카테고리 "+ final_selection , Toast.LENGTH_SHORT).show();
                post_selected_category.setText(final_selection);
                getCategories();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        post_selected_category.setText(" ");
        String[] cate = new String[5];
        if (data.getStringExtra("casual")!=null){cate[0]="캐쥬얼 ";}
        else{cate[0]="";}
        if (data.getStringExtra("minimal")!=null){cate[1]="미니멀 ";}
        else{cate[1]="";}
        if (data.getStringExtra("american")!=null){cate[2]="아메카지 ";}
        else{cate[2]="";}
        if (data.getStringExtra("street")!=null){cate[3]="스트릿 ";}
        else{cate[3]="";}
        if (data.getStringExtra("etc")!=null){cate[4]="기타 ";}
        else{cate[4]="";}


        for(int i = 0; i <= 4 ; i++){
            post_selected_category.append(cate[i]);
        }


    }

//    상단 뒤로가기
//    View.OnClickListener back_to = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            postMethods.onBackPresse(PostActivity.this);
//        }
//    };


    // 사진 갤러리에서 선택
    View.OnClickListener get_post_img = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/");
            activityResult.launch(galleryIntent);
        }
    };

    ActivityResultLauncher<Intent> activityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()== RESULT_OK && result.getData() != null){
                        imageUri = result.getData().getData();
                        post_img.setImageURI(imageUri);
                        System.out.println("이미지 : " +imageUri);
                        try {
                            ExifInterface exif = new ExifInterface(getRealPathFromURI(imageUri));
                            showExif(exif);
                            getAddress();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
    // 사진 갤러리에서 선택

    private void showExif(ExifInterface exif) {
        attrLATITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
        attrLATITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
        attrLONGITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        attrLONGITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
        attrDate =exif.getAttribute(ExifInterface.TAG_DATETIME);

        postdate.setText(attrDate);
        if((attrLATITUDE!=null)&&(attrLATITUDE_REF!=null)&&(attrLONGITUDE!=null) &&(attrLONGITUDE_REF!=null)){
            valid=true;
            if(attrLATITUDE_REF.equals("N")){
                lat=convertToDegree(attrLATITUDE);
            }
            else{
                lat=0-convertToDegree(attrLATITUDE);
            }
            if(attrLONGITUDE_REF.equals("E")){
                lon=convertToDegree(attrLONGITUDE);
            }
            else{
                lon=0-convertToDegree(attrLONGITUDE);
            }
        }
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        if(attrDate.contains(today)){//사진의 메타데이터와 오늘 날짜가
            // 같을 경우 (아직 openweathermap)
            postMethods.getWeatherNow(PostActivity.this, lat, lon, attrDate,temp,mintemp,maxtemp, postdate);
        }else{ // 아닌 경우 종관asos 이전 날씨 조회
            postMethods.getWeatherBefore(PostActivity.this, lat, lon, attrDate,temp,mintemp,maxtemp, postdate);
        }
    }

    private String getRealPathFromURI(Uri uri){
        String result;
        Cursor cursor=getContentResolver().query(uri,null,null,null,null);
        if(cursor==null){
            result = uri.getPath();
        }
        else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private Float convertToDegree(String stringDMS) {
        Float result = null;
        String [] DMS = stringDMS.split(",",3);

        String[] stringD = DMS[0].split("/",2);
        Double D0 = Double.valueOf(stringD[0]);
        Double D1 = Double.valueOf(stringD[1]);
        Double FloatD = D0/D1;

        String[] stringM = DMS[1].split("/",2);
        Double M0 = Double.valueOf(stringM[0]);
        Double M1 = Double.valueOf(stringM[1]);
        Double FloatM = M0/M1;

        String[] stringS = DMS[2].split("/",2);
        Double S0 = Double.valueOf(stringS[0]);
        Double S1 = Double.valueOf(stringS[1]);
        Double FloatS = S0/S1;

        result = (float) (FloatD + (FloatM / 60) + (FloatS / 3600));

        return result;
    }

    public Float getLatitude() {
        return lat;
    }

    public Float getLongitude() {
        return lon;
    }

    public String getAddress() {
        List<Address> address=null;
        try {
            address = g.getFromLocation(getLatitude(),getLongitude(),10);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("test","입출력오류");
        }
        if(address!=null){
            if(address.size()==0){
                Log.d("test", "주소찾기 오류");
            }else{
                Log.d("찾은 주소",address.get(0).getAddressLine(0));
                System.out.println(address.get(0).getAddressLine(0)+"주소");
                String s = address.get(0).getAddressLine(0);
                String location = s.substring(s.indexOf(" "));
                location = location.substring(0,location.lastIndexOf(" "));
                location = location.substring(0,location.lastIndexOf(" "));
                postlocation.setText(location);
                postlocation.setVisibility(View.VISIBLE);
                return address.get(0).getAddressLine(0);
            }
        }
        return null;
    }
    public void PostImage(){
        Uri file = imageUri;
        System.out.println("tt : "+image_uri);
        try {
            image_str = String.valueOf(imageUri).substring(String.valueOf(imageUri).lastIndexOf("/") + 1);
            System.out.println("image_str: " + image_str);
            StorageReference riversRef = storageRef.child("post");
            StorageReference riversRef2 = riversRef.child(file.getLastPathSegment());
            UploadTask uploadTask = riversRef2.putFile(file);
            image_uri = image_str;
// Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                }
            });

        } catch (NullPointerException e){
            e.printStackTrace();
        }
//    riversRef.child(image_str).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//        @Override
//        public void onSuccess(Uri uri) {
//
//            System.out.println("uri : "+uri);
//            Glide.with(PostActivity.this /* context */)
//                    .load(uri)
//                    .into(imageView2);
//        }
//    }).addOnFailureListener(new OnFailureListener() {
//        @Override
//        public void onFailure(@NonNull Exception exception) {
//            // Handle any errors
//        }
//    });


// Download directly from StorageReference using Glide
// (See MyAppGlideModule for Loader registration)


    }


    public void getName() {
        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    if(snapshot1.child("user_email").getValue().toString().equals(user.getEmail())){
                        user_name = snapshot1.child("user_name").getValue().toString();
                        user_gender = snapshot1.child("user_gender").getValue().toString();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setPost(){
        Date date = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        boolean b = TextUtils.isEmpty(image_uri);
        if(post_main_text.getText().length()==0){
            api.getToast(this,"내용을 입력해주세요.");
        }else if(b){
            api.getToast(this,"이미지를 업로드해주세요.");
        }else if(post_categories.size() == 0){
            post_categories.add("미니멀");
            post_categories.add("캐주얼");
            api.getToast(this,"카테고리를 선택해주세요.");
        }else {

            System.out.println("이미지  : " + image_str);

            String h = postdate.getText().toString().replace(" ","");
            h = h.replace("-","");
            h = h.replace(":","");


            String content = post_main_text.getText().toString();
            String image = image_uri;
            String user_name_str = user_name;

            String post_min_temp = mintemp.getText().toString();
            int minTempIdx = post_min_temp.indexOf("°");
            post_min_temp = post_min_temp.substring(0, minTempIdx);

            String post_max_temp = maxtemp.getText().toString();
            int maxTempIdx = post_max_temp.indexOf("°");
            post_max_temp = post_max_temp.substring(0, maxTempIdx);

            String post_temp = temp.getText().toString();
            int TempIdx = post_temp.indexOf("°");
            post_temp = post_temp.substring(0, TempIdx);

            String location = postlocation.getText().toString();
            String post_date = h;
            String now_date = sdf.format(date);
            Long post_likeCount = 0L;
            HashMap<String, Boolean> post_likes = null;
            String postuserid = user.getUid();
            Post post = new Post(content, image, user_name_str, post_min_temp, post_max_temp, post_temp, location
                    , post_date, now_date, post_likeCount, post_likes, post_categories, postuserid, user_gender);
            mDatabase.child("post").push().setValue(post);

            api.getToast(this,"업로드 성공");
            Intent intent =new Intent(this,MainActivity.class);
            startActivity(intent);
        }
    }
    public void getCategories(){
        String [] str = final_selection.trim().split(" ");
        post_categories.clear();
        for(int j = 0; j<str.length; j++){
            post_categories.add(str[j]);
        }
    }
}