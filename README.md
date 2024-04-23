# WeatherFashion
:날씨 API를 이용한 패션 커뮤니티


마이페이지 만드는 소스 코드입니다.
```java

public class MyInfoFragment extends Fragment {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    String image_uri = null;
    String image_str = null;
    ArrayList<String> chatIdList = new ArrayList<>();
    HashMap<String, Boolean> chatMap = new HashMap<String, Boolean>();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    StorageReference riversRef = storageRef.child("profile");
    AlertDialog.Builder builder;
    RecyclerView.Adapter adapter;
    ArrayList<MyInfoList> list = new ArrayList();
    RecyclerView recyclerView;
    GridLayoutManager layoutManager;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private Uri imageUri = null;
    TextView myname, mycomment, back_pressed;
    ImageView setting, chat_image, alert;
    CircleImageView myprofile;
    Button post_write_btn;
    Button btn_modify;
    EditText dlg_edit_intro;
    API api = new API();



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private AlertDialog AlertDialog;

    public MyInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyInfoFragment newInstance(String param1, String param2) {
        MyInfoFragment fragment = new MyInfoFragment();
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
        ViewGroup view = (ViewGroup)inflater.inflate(R.layout.fragment_myinfo, container, false);
        myname = view.findViewById(R.id.my_name);
        mycomment = view.findViewById(R.id.my_comment);
        myprofile = view.findViewById(R.id.my_profile);
        setting = view.findViewById(R.id.setting);
        setting.setOnClickListener(getProfileimg);
        post_write_btn = view.findViewById(R.id.post_write_btn);
        post_write_btn.setOnClickListener(btnListener);
        btn_modify = view.findViewById(R.id.btn_modify);
        chat_image = view.findViewById(R.id.chat_image);
        alert = view.findViewById(R.id.alert);
        btn_modify.setOnClickListener(btnListener_m);
        recyclerView = view.findViewById(R.id.myinfo_recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getActivity(),3);
        recyclerView.setLayoutManager(layoutManager);
        chat_image.setOnClickListener(btnListener);

        setAlert();
        setProfile();
        getMyPostList();
        // Inflate the layout for this fragment
        return view;
    }

    public void setAlert(){
        mDatabase.child("chatrooms").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1 : snapshot.getChildren()){

                    System.out.println(chatMap);
                    if (snapshot1.child("alert").getValue() != null){
                        chatMap = (HashMap<String, Boolean>) snapshot1.child("alert").getValue();
                        if (chatMap.containsKey(user.getUid())&&chatMap.get(user.getUid())){

                            alert.setVisibility(View.VISIBLE);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.post_write_btn:

                    Intent intent2 = new Intent(getContext(), PostActivity.class);
                    startActivity(intent2);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
                case R.id.chat_image:
                    alert.setVisibility(View.GONE);
                    FragmentManager fm = ((MainActivity) getContext()).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction;
                    ChatListFragment chatFragment = new ChatListFragment();
                    fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.addToBackStack(null)
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .replace(R.id.main_ly, chatFragment)
                            .commit();
            }
        }

    };




    View.OnClickListener btnListener_m = new View.OnClickListener() {
        @SuppressLint("SuspiciousIndentation")
        @Override
        public void onClick(View view) {
            builder = new AlertDialog.Builder(getContext());
            builder.setTitle("한줄 소개");

            View dialogView = View.inflate(getContext(),R.layout.dialog_modify,null);
            builder.setView(dialogView);
            dlg_edit_intro = dialogView.findViewById(R.id.dlg_edit_intro);


            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                   mDatabase.child("users").child(user.getUid()).child("user_comment").setValue(dlg_edit_intro.getText().toString());
                   api.getToast(getActivity(),"한줄 소개 수정완료");
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
        };


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void getMyPostList(){
        mDatabase.child("post").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    if (snapshot1.child("post_user_id").getValue().equals(user.getUid())) {
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

    public void setProfile(){
        mDatabase.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println(snapshot.getValue());
                myname.setText(snapshot.child("user_name").getValue().toString());
                mycomment.setText(snapshot.child("user_comment").getValue().toString());
                String myprofile_str = snapshot.child("user_profile").getValue().toString();
                riversRef.child(myprofile_str).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getActivity()).load(uri)
                                .into(myprofile);
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

    View.OnClickListener getProfileimg = new View.OnClickListener() {
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
                        myprofile.setImageURI(imageUri);
                        System.out.println("이미지 : " +imageUri);
                        System.out.println();
                        mDatabase.child("users").child(user.getUid()).child("user_profile").setValue(imageUri.toString().substring(imageUri.toString().lastIndexOf("/")+1));
                        PostImage();
                        try {
                            ExifInterface exif = new ExifInterface(getRealPathFromURI(imageUri));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private String getRealPathFromURI(Uri uri){
        String result;
        Cursor cursor = getActivity().getContentResolver().query(uri,null,null,null,null);
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


    public void PostImage() {
        Uri file = imageUri;
        System.out.println("tt : " + image_uri);
        try {
            image_str = String.valueOf(imageUri).substring(String.valueOf(imageUri).lastIndexOf("/") + 1);
            System.out.println("image_str: " + image_str);
            StorageReference riversRef = storageRef.child("profile");
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

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    }


```

리사이클러뷰 기능을 이용하여 이용자들에게 날씨를 편리하게 알려주기 위한 코드입니다.
```java

recyclerView.setAdapter(adapter);
```

```java
public class DailyWeatherAdapter extends RecyclerView.Adapter<DailyWeatherAdapter.ViewHolder> {

    private ArrayList<Weather> mData = null ;
    private Context context;

    public DailyWeatherAdapter(Context context, ArrayList<Weather> mData) {
        this.mData = mData;
        this.context = context;
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView time ;
        ImageView icon;
        TextView temp;

        public ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            time = itemView.findViewById(R.id.time);
            icon = itemView.findViewById(R.id.icon);
            temp = itemView.findViewById(R.id.temp);
        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    public DailyWeatherAdapter(ArrayList<Weather> list) {
        mData = list ;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public DailyWeatherAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_weather_recycler_view_item, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder ;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(DailyWeatherAdapter.ViewHolder holder, int position) {
        String temp = mData.get(position).getNow_Temp() + "°";
        ArrayList<String> morning = new ArrayList<>(Arrays.asList("오전 7시","오전 8시", "오전 9시","오전 10시","오전 11시","오후 12시","오후 1시",
                "오후 2시","오후 3시","오후 4시","오후 5시", "오후 6시"));
        ArrayList<String> night = new ArrayList<>(Arrays.asList("오전 00시","오전 1시","오전 2시","오전 3시","오전 4시","오전 5시","오전 6시",
                "오후 7시","오후 8시","오후 9시","오후 10시","오후 11시"));
        String sky = mData.get(position).getSky();
        String pty = mData.get(position).getPty();
        String time = mData.get(position).getTime();
        holder.time.setText(time);
        holder.temp.setText(temp);

```
