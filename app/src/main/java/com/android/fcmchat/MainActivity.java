package com.android.fcmchat;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.fcmchat.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    ArtistAdapter adapter;
    DatabaseReference databaseReference;
    private List<Artist> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);

        databaseReference = FirebaseDatabase.getInstance().getReference("artist");

        submitButtonClick();

    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                itemList.clear();
                for (DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                    Artist artist = artistSnapshot.getValue(Artist.class);
                    itemList.add(artist);
                }
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
                binding.recycler.setLayoutManager(linearLayoutManager);
                adapter = new ArtistAdapter(itemList, MainActivity.this);
                binding.recycler.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void submitButtonClick() {

        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String sName = binding.name.getText().toString().trim();
                if (validation(sName)) {
                    //uniquily generated ID
                    String id = databaseReference.push().getKey();
                    Artist artist = new Artist();
                    artist.setArtistId(id);
                    artist.setArtistName(sName);
                    databaseReference.child(id).setValue(artist);
                    Toast.makeText(MainActivity.this, "added", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void showUpdateDialog(String artistId, String artistName) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_dialog, null);

        dialogBuilder.setView(dialogView);
        final EditText edtName = (EditText) dialogView.findViewById(R.id.edt_update);
        final Button updateButton = (Button) dialogView.findViewById(R.id.btn_update);
        final Button deleteButton = (Button) dialogView.findViewById(R.id.delete);

        dialogBuilder.setTitle("Updating Artist");
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        buttonUpdateClick(updateButton, edtName, artistId, artistName, alertDialog);
        deleteButtonClick(deleteButton, artistId, alertDialog);

    }

    private void deleteButtonClick(Button deleteButton, final String artistId, final AlertDialog alertDialog) {
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteArtist(artistId);
                alertDialog.dismiss();
            }
        });
    }

    private void deleteArtist(String artistId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("artist").child(artistId);
        databaseReference.removeValue();
        Toast.makeText(this, "delete successfully", Toast.LENGTH_SHORT).show();
    }

    private void buttonUpdateClick(Button updateButton, final EditText edtName, final String artistId, final String artistName, final AlertDialog alertDialog) {
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtName.getText().toString().trim();
                if (name.isEmpty()) {
                    edtName.setError("enter name");
                } else {
                    updateArtist(artistId, name);
                    alertDialog.dismiss();
                }
            }
        });
    }

    private boolean updateArtist(String artistId, String artistName) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("artist").child(artistId);

        Artist artist = new Artist(artistId, artistName);
        databaseReference.setValue(artist);
        Toast.makeText(this, "Updated Successfully", Toast.LENGTH_SHORT).show();
        return true;
    }

    private boolean validation(String name) {
        if (name.isEmpty()) {
            binding.name.setError("enter name");
            return false;
        }
        return true;
    }

    public void viewItemClick(int position) {
        Artist artist = itemList.get(position);
        showUpdateDialog(artist.artistId, artist.getArtistName());
    }
}

/*

FCM rules auth
        {
        "rules": {
        ".read": "auth != null",
        ".write": "auth != null"
        }
        }*/
