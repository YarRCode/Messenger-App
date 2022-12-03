package com.firestoremessenger.messengerapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Intent intent;
    int id;

    ListView listView;
    CustomListAdapter customListAdapter;
    EditText messageEditText;

    ArrayList<String> messageList;
    ArrayList<String> emailList;

    String[] messageListToArr;
    String[] emailToString;

    String cout;
    String massageToString;
    String userEmail;

    private FirebaseFirestore dp = FirebaseFirestore.getInstance();
    private DocumentReference docRef = dp.collection("data").document("message");
    private static String KEY_NUMBER_OF_STORES = "Number of stores";

    @Override
    protected void onStart() {
        super.onStart();
        docRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.exists()){
                    docRef.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists()){
                                        messageList.clear();
                                        emailList.clear();
                                        String numberOfStoresToString = documentSnapshot.getString(KEY_NUMBER_OF_STORES);
                                        cout = numberOfStoresToString;
                                        int numberOfStores = Integer.parseInt(numberOfStoresToString);
                                        for(int i = 0; i < numberOfStores; i++){
                                            String iToString = Integer.toString(i);
                                            String iForEmail = iToString+"email";
                                            messageList.add(documentSnapshot.getString(iToString));
                                            emailList.add(documentSnapshot.getString(iForEmail));
                                        }
                                        messageListToArr = messageList.toArray(new String[0]);
                                        emailToString = emailList.toArray(new String[0]);
                                        customListAdapter = new CustomListAdapter(getApplicationContext(), emailToString, messageListToArr);
                                        listView.setAdapter(customListAdapter);
                                    }  else{
                                        Toast.makeText(MainActivity.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    Log.d("Error", e.toString());
                                }
                            });
                }  else{
                    Toast.makeText(MainActivity.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    void init(){

        user = FirebaseAuth.getInstance().getCurrentUser();
        listView = findViewById(R.id.listView);
        messageList = new ArrayList<>();
        emailList = new ArrayList<>();
        messageEditText = findViewById(R.id.messageEditText);
        if(user == null){
            startLoginActivity();
        }
        else {
            userEmail = user.getEmail();
        }
    }

    public void createMsg(View v) {
        massageToString = messageEditText.getText().toString();
        if(massageToString.isEmpty()) { return; }
        messageList.add(massageToString);
        emailList.add(user.getEmail());
        saveData();
    }

    private void saveData() {
        Map<String, Object> hashMap = new HashMap<>();

        messageListToArr = messageList.toArray(new String[0]);
        emailToString = emailList.toArray(new String[0]);

        cout = Integer.toString(messageListToArr.length);
        hashMap.put(KEY_NUMBER_OF_STORES, cout);

        for (int i = 0; i < messageListToArr.length; i++) {
            String iToString = Integer.toString(i);
            String iForEmail = iToString+"email";

            hashMap.put(iForEmail, emailToString[i]);
            hashMap.put(iToString, messageListToArr[i]);
        }

        docRef.set(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        Log.d("Error", e.toString());
                    }
                });
    }

    private void startLoginActivity() {
        intent = new Intent(this, LoginRegisterActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        id = item.getItemId();

        switch (id) {
            case(R.id.logoutitem):
                Toast.makeText(this, "Log out", Toast.LENGTH_SHORT).show();
                AuthUI.getInstance().signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    startLoginActivity();
                                }
                            }
                        });
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}