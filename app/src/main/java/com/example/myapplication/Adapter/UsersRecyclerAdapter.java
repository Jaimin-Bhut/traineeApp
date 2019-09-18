package com.example.myapplication.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activities.SignInActivity;
import com.example.myapplication.model.User;
import com.example.myapplication.sql.DatabaseHelper;

import java.util.List;

import static android.widget.Toast.*;

public class UsersRecyclerAdapter extends RecyclerView.Adapter<UsersRecyclerAdapter.UserViewHolder> {
    private List<User> listUsers;
    private DatabaseHelper databaseHelper;
    private Context context;


    public UsersRecyclerAdapter(Context context,List<User> listUsers, DatabaseHelper databaseHelper) {
        this.listUsers = listUsers;
        this.context = context;
        this.databaseHelper = databaseHelper;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflating recycler item view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_card_layout, parent, false);

        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, final int position) {

        holder.textViewFname.setText(listUsers.get(position).getFirst_name());
        holder.textViewLname.setText(listUsers.get(position).getLast_name());
        holder.textViewEmail.setText(listUsers.get(position).getEmail());
        holder.btnDelete.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                String email = sharedPreferences.getString( "email", "" );
                if (listUsers.get(position).getEmail().equals(email)){

                    Toast.makeText(context,"User is Already Login", LENGTH_SHORT).show();
                }else {
                    Log.e("onClick: ", "");
                    databaseHelper.deleteUser(listUsers.get(position).getEmail());
                    notifyDataSetChanged();
                }
            }
        } );

    }

    @Override
    public int getItemCount() {
        Log.v(UsersRecyclerAdapter.class.getSimpleName(), "" + listUsers.size());
        return listUsers.size();
    }


    /**
     * ViewHolder class
     */
    public class UserViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewFname;
        public TextView textViewLname;
        public TextView textViewEmail;
        public Button btnDelete;

        public UserViewHolder(View view) {
            super(view);
            textViewFname = (TextView) view.findViewById(R.id.textFname);
            textViewLname = (TextView) view.findViewById(R.id.textLname);
            textViewEmail = (TextView) view.findViewById(R.id.textEmail);
            final String Email = textViewEmail.getText().toString();

            btnDelete = view.findViewById( R.id.btn_delete);

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (textViewEmail.equals(databaseHelper.checkUser(Email))){
                    }
                    else {
//                        Toast.makeText(UsersRecyclerAdapter.this,"User is Already Login", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }
}
