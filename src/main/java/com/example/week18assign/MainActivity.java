package com.example.week18assign;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // fields
    private EditText scoreNumber;
    private Button scoreButton;

    // object
    Highscore highscore;

    // connect to firebase
    final static String points = "points";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // method for getting highscore from firebase
    private void startPointListener() {
        // goes through firebase collection called points
        db.collection(points).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot values, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot snap: values.getDocuments()) {
                    Log.i("info", "read from firebase: " + snap.getId() +" "+ snap.get("clicks"));
                    highscore = new Highscore(snap.getId(), snap.getDouble("clicks"));
                    scoreNumber.setText(""+ (int)highscore.getScore());
                }
            }

        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // gets elements from view
        scoreNumber = findViewById(R.id.scoreNumber);
        scoreButton = findViewById(R.id.scoreButton);

        // initializes method on application start
        startPointListener();

        // listens for button press and adds to highscore
        scoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // converts double to int
                int highscoreValue = (int)highscore.getScore();

                // inserts value to view
                scoreNumber.setText(""+ highscoreValue++);

                // gets the correct document in firebase
                DocumentReference docRef = db.collection(points).document(highscore.getId());
                System.out.println(docRef);

                // input in map which holds the value(highscore) and key(document id)
                Map<String, Integer> map = new HashMap<>();
                map.put("clicks", highscoreValue);
                docRef.set(map);
            }
        });
    }
}