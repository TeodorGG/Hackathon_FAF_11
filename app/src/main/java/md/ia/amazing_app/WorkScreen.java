package md.ia.amazing_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import md.ia.amazing_app.model.Mask;
import md.ia.amazing_app.model.Session;
import md.ia.amazing_app.model.User;
import md.ia.amazing_app.model.bg;

public class WorkScreen extends AppCompatActivity {

    String json, name, email;

    long time = 1000 * 60 * 10 ;

    RecyclerView recyclerView;
    RecyclerView recyclerView1;
    RecyclerView recyclerView2;

    boolean isLoges = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_screen);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView1 = findViewById(R.id.recyclerview2);
        recyclerView2 = findViewById(R.id.recyclerview3);

        initReciclerViewIbalnic();
        initReciclerViewBack();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            json = extras.getString("json");
            name = extras.getString("name");
            email = extras.getString("email");
        }

//        Toast.makeText(this, json + " | " + name + " | " + email, Toast.LENGTH_SHORT).show();


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        try {
            JSONObject o = new JSONObject(json);

            mDatabase.child("session").addValueEventListener(new ValueEventListener(){

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Toast.makeText(WorkScreen.this, "Update", Toast.LENGTH_SHORT).show();


                    if(dataSnapshot.hasChildren()){
                        updateReciclerView(dataSnapshot);
                        Iterator<DataSnapshot> iter = dataSnapshot.getChildren().iterator();
                        DataSnapshot snap = iter.next();
                        if(snap.hasChildren() && snap.getValue() instanceof HashMap) {
                            HashMap map = (HashMap) snap.getValue();
                            if(map.containsKey("index")) {
                                try {
                                    if (map.get("index").toString().equals(String.valueOf(o.getInt("index")))) {

                                        if(System.currentTimeMillis() > Long.parseLong(map.get("endSession").toString())){

                                            Session session = new Session(System.currentTimeMillis(), System.currentTimeMillis() + time, name, email, o.getInt("index"), 0, 0);
                                            Map<String, Object> postValues = session.toMap();
                                            Map<String, Object> childUpdates = new HashMap<>();
                                            childUpdates.put("/session/" + o.getInt("index"), postValues);
                                            mDatabase.updateChildren(childUpdates);

                                            Map<String, Object> postValues1 =  new HashMap<>();
                                            Map<String, Object> childUpdates1 = new HashMap<>();
                                            for(User u : session.users){
                                                String key = System.currentTimeMillis() +"";
                                                postValues1.put(key, u.toMap());
                                            }
                                            isLoges = true;

                                            childUpdates1.put("/session/" + o.getInt("index") + "/users/", postValues1);
                                            mDatabase.updateChildren(childUpdates1);
                                        } else {

                                            try{
                                                if(!Objects.requireNonNull(map.get("users")).toString().contains(email)){
                                                    isLoges = true;
                                                    Map<String, Object> postValues1 =  new HashMap<>();
                                                    Map<String, Object> childUpdates1 = new HashMap<>();

                                                    postValues1.put("name", name);
                                                    postValues1.put("email", email);

                                                    childUpdates1.put("/session/" + o.getInt("index") + "/users/" + System.currentTimeMillis(), postValues1);
                                                    mDatabase.updateChildren(childUpdates1);
                                                } else {
                                                    if(!isLoges){
                                                        Toast.makeText(WorkScreen.this,  "Your email is logged!", Toast.LENGTH_SHORT).show();
                                                        onBackPressed();
                                                    }
                                                }
                                            } catch (Exception ignored){}
                                        }
                                    } else {
                                        Session session = new Session(System.currentTimeMillis(), System.currentTimeMillis() + time, name, email, o.getInt("index"), 0,0);
                                        Map<String, Object> postValues = session.toMap();
                                        Map<String, Object> childUpdates = new HashMap<>();
                                        childUpdates.put("/session/" + o.getInt("index"), postValues);
                                        mDatabase.updateChildren(childUpdates);
                                        isLoges = true;

                                        Map<String, Object> postValues1 =  new HashMap<>();
                                        Map<String, Object> childUpdates1 = new HashMap<>();
                                        for(User u : session.users){
                                            String key =  System.currentTimeMillis()+"";
                                            postValues1.put(key, u.toMap());
                                        }

                                        childUpdates1.put("/session/" + o.getInt("index") + "/users/", postValues1);
                                        mDatabase.updateChildren(childUpdates1);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {
                        try {
                            Session session = new Session(System.currentTimeMillis(), System.currentTimeMillis() + time, name, email, o.getInt("index"), 0,0);
                            Map<String, Object> postValues = session.toMap();
                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put("/session/" + o.getInt("index"), postValues);
                            mDatabase.updateChildren(childUpdates);
                            isLoges = true;

                            Map<String, Object> postValues1 =  new HashMap<>();
                            Map<String, Object> childUpdates1 = new HashMap<>();
                            for(User u : session.users){
                                String key =  System.currentTimeMillis()+"";
                                postValues1.put(key, u.toMap());
                            }

                            childUpdates1.put("/session/" + o.getInt("index") + "/users/", postValues1);
                            mDatabase.updateChildren(childUpdates1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void initReciclerViewBack() {
        ArrayList<bg> bgs = new ArrayList<>();

        bgs.add(new bg(0, R.drawable.utm));
        bgs.add(new bg(1, R.drawable.bg1));
        bgs.add(new bg(2, R.drawable.bg2jpg));
        bgs.add(new bg(3, R.drawable.bg3));
        bgs.add(new bg(4, R.drawable.bg4));
        bgs.add(new bg(5, R.drawable.bg5));
        bgs.add(new bg(6, R.drawable.bg6));
        bgs.add(new bg(7, R.drawable.bg7));


        recyclerView2.setLayoutManager(new LinearLayoutManager(this));

        recyclerView2.setAdapter(new RecyclerView.Adapter<EbalnicViewHolder>() {

            @Override
            public EbalnicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_images,
                        parent,
                        false);
                EbalnicViewHolder vh = new EbalnicViewHolder(v);
                return vh;
            }

            @Override
            public void onBindViewHolder(EbalnicViewHolder vh, int position) {
                vh.image.setBackgroundResource(bgs.get(position).bg_image);
                vh.image.setOnClickListener( v -> {
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    JSONObject o = null;
                    try {
                        o = new JSONObject(json);

                        mDatabase.child("session").child( o.getInt("index")+"").child("bg").setValue(bgs.get(position).bg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public int getItemCount() {
                return bgs.size();
            }
        });
    }

    private void initReciclerViewIbalnic() {
        ArrayList<Mask> mask = new ArrayList<>();
        mask.add(new Mask(1, R.drawable.cosmos));
        mask.add(new Mask(2, R.drawable.ph1));
        mask.add(new Mask(3, R.drawable.ph2));
        mask.add(new Mask(4, R.drawable.ph3));
        mask.add(new Mask(5, R.drawable.ph4));
        mask.add(new Mask(6, R.drawable.ph5));
        mask.add(new Mask(7, R.drawable.ph6));

        recyclerView1.setLayoutManager(new LinearLayoutManager(this));

        recyclerView1.setAdapter(new RecyclerView.Adapter<EbalnicViewHolder>() {

            @Override
            public EbalnicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_images,
                        parent,
                        false);
                EbalnicViewHolder vh = new EbalnicViewHolder(v);
                return vh;
            }

            @Override
            public void onBindViewHolder(EbalnicViewHolder vh, int position) {
                vh.image.setBackgroundResource(mask.get(position).ph_image);
                vh.image.setOnClickListener( v -> {
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    JSONObject o = null;
                    try {
                        o = new JSONObject(json);

                        mDatabase.child("session").child( o.getInt("index")+"").child("ph").setValue(mask.get(position).ph);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                });
            }

            @Override
            public int getItemCount() {
                return mask.size();
            }
        });
    }

    class EbalnicViewHolder
            extends RecyclerView.ViewHolder
    {
        public ImageFilterView image;

        public EbalnicViewHolder(View v) {
            super(v);
            image = v.findViewById(R.id.iv_image);
        }
    }

    private void updateReciclerView(DataSnapshot dataSnapshot) {

        ArrayList<String> a = new ArrayList<>();

        if(dataSnapshot.hasChildren()){
            Iterator<DataSnapshot> iter = dataSnapshot.getChildren().iterator();
            while (iter.hasNext()){
                DataSnapshot snap = iter.next();

                Iterator<DataSnapshot> iter1 = snap.child("users").getChildren().iterator();

                while (iter1.hasNext()){
                    DataSnapshot snap1 = iter1.next();
                    a.add((String) snap1.child("name").getValue().toString());
                }

            }

        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(new RecyclerView.Adapter<UserViewHolder>() {

            @Override
            public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(
                        android.R.layout.simple_list_item_1,
                        parent,
                        false);
                UserViewHolder vh = new UserViewHolder(v);
                return vh;
            }

            @Override
            public void onBindViewHolder(UserViewHolder vh, int position) {
                TextView tv = (TextView) vh.itemView;
                tv.setText(a.get(position));
            }

            @Override
            public int getItemCount() {
                return a.size();
            }
        });
    }

    class UserViewHolder
            extends RecyclerView.ViewHolder
          {

        public UserViewHolder(View v) {
            super(v);
        }
    }


}

