package com.example.myapplication.activities;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapter.UsersRecyclerAdapter;
import com.example.myapplication.R;
import com.example.myapplication.model.User;
import com.example.myapplication.sql.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {
    private RecyclerView recyclerViewUsers;
    private AppCompatActivity activity = UsersActivity.this;
    private List<User> listUsers;
    private UsersRecyclerAdapter usersRecyclerAdapter;
    private DatabaseHelper databaseHelper;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        initViews();

    }

    private void initViews() {
        recyclerViewUsers = (RecyclerView) findViewById(R.id.recycler_view_user);
        listUsers = new ArrayList<>();
        databaseHelper = new DatabaseHelper(activity);
        usersRecyclerAdapter = new UsersRecyclerAdapter(this,listUsers, databaseHelper);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewUsers.setLayoutManager(mLayoutManager);
        recyclerViewUsers.setItemAnimator(new DefaultItemAnimator());
        recyclerViewUsers.setHasFixedSize(true);
        recyclerViewUsers.setAdapter(usersRecyclerAdapter);
        listUsers.addAll(databaseHelper.getAllUser());
        usersRecyclerAdapter.notifyDataSetChanged();
    }
}

