package com.umarappdel.earningapk.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.umarappdel.earningapk.R;
import com.umarappdel.earningapk.RedeemActivity;
import com.umarappdel.earningapk.model.ProfileModel;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.umarappdel.earningapk.model.Veriables.JazzCash;
import static com.umarappdel.earningapk.model.Veriables.JazzCashImageURL;


public class AmazonFragment extends Fragment {

    private EditText nameEt, phoneEt;
    private TextView coinsTv;
    private Button sendBtn;
    private RadioGroup radioGroup;
    private Dialog dialog;
    private DatabaseReference reference;
    private FirebaseUser user;
    private CircleImageView circleImageView;

    public AmazonFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_amazon, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
        loadData();
        loadImage();

        clickListener();

    }

    private void loadImage() {

        Glide.with(AmazonFragment.this)
                .load(JazzCashImageURL)
                .into(circleImageView);
    }

    private void init(View view) {
        radioGroup = view.findViewById(R.id.radioGroup);
        sendBtn = view.findViewById(R.id.submitBtn);
        coinsTv = view.findViewById(R.id.coinsTv);

        nameEt = view.findViewById(R.id.nameET);
        phoneEt = view.findViewById(R.id.numberET);
        circleImageView = view.findViewById(R.id.JazzCash);

        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.loading_dialog);
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        reference = FirebaseDatabase.getInstance().getReference().child("Users");

        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

    }

    private void loadData() {

        reference.child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        ProfileModel model = snapshot.getValue(ProfileModel.class);
                        coinsTv.setText(String.valueOf(model.getCoins()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }
                });

    }

    private void clickListener() {

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = nameEt.getText().toString();
                String phone = phoneEt.getText().toString();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)) {
                    Toast.makeText(getContext(), "Fill all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                int id = radioGroup.getCheckedRadioButtonId();

                switch (id) {

                    case R.id.phone50:
                        checkCoin(name, phone, 50);
                        break;

                    case R.id.phone100:
                        checkCoin(name, phone, 100);
                        break;
                }


            }
        });

    }

    DatabaseReference withdrawRef;

    private void checkCoin(String name, String phone, int amount){

        int current = Integer.parseInt(coinsTv.getText().toString());

        if (amount == 50) {


            if (current < 6000) {
                Toast.makeText(getContext(), "You do not have enough coins", Toast.LENGTH_SHORT).show();
                return;
            }
            sendWithdrawRequest(name, phone, amount);
        }

        if (amount == 100) {


            if (current < 1200) {
                Toast.makeText(getContext(), "You do not have enough coins", Toast.LENGTH_SHORT).show();
                return;
            }
            sendWithdrawRequest(name, phone, amount);


        }


    }

    private void sendWithdrawRequest(String name, String phone, int amount) {

        withdrawRef = FirebaseDatabase.getInstance().getReference().child("Withdraw").child(user.getUid());

        String id = withdrawRef.push().getKey();

        HashMap<String, Object> map = new HashMap<>();

        if (amount == 50) {
            map.put("amount", 50);
        }
        if (amount == 100) {
            map.put("amount", 100);
        }

        //image url

        map.put("phone", phone);
        map.put("name", name);
        map.put("id", id);
        map.put("type", "Mobile Pay");
        map.put("status", "Pending");
        map.put("image", JazzCash);
        map.put("uid", user.getUid());

        int current = Integer.parseInt(coinsTv.getText().toString());
        int update = 0;

        if (amount == 50)
            update = current - 6000;

        if (amount == 100)
            update = current - 6000;

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("coins", update);

        reference.child(user.getUid())
                .updateChildren(userMap);

        withdrawRef.child(id).setValue(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Congrats", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}