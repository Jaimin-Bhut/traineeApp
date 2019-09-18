package com.example.myapplication.Adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.myapplication.R;
import com.example.myapplication.model.User;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {

        private List<User> users;
        private Context mContext;

        public ContactsAdapter(List<User> contectModelList, Context mContext) {
            this.users = contectModelList;
            this.mContext = mContext;
        }


    @Override
        public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.activity_card_contacts, null);
            ContactViewHolder contactViewHolder = new ContactViewHolder(view);
            return contactViewHolder;
        }

        @Override
        public void onBindViewHolder(ContactViewHolder holder, int position) {
            User contactVO = users.get(position);
            holder.tvContactName.setText(contactVO.getContactName());
            holder.tvPhoneNumber.setText(contactVO.getPhoneNumber());
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        public class ContactViewHolder extends RecyclerView.ViewHolder{

            TextView tvContactName;
            TextView tvPhoneNumber;

            public ContactViewHolder(View itemView) {
                super(itemView);
                tvContactName = itemView.findViewById(R.id.textContactName);
                tvPhoneNumber = itemView.findViewById(R.id.textPhoneNumber);
            }
        }
    }
