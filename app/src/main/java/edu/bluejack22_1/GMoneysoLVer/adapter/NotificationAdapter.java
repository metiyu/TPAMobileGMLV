package edu.bluejack22_1.GMoneysoLVer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tpamobile.R;

import edu.bluejack22_1.GMoneysoLVer.model.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private Context c;
    private List<Notification> notificationList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();

    public NotificationAdapter(Context c, List<Notification> notificationList){
        this.c = c;
        this.notificationList = notificationList;
    }
    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.NotificationViewHolder holder, int position) {
        Log.d("onBindViewHolder", "onBindViewHolder: " + c.getClass().getName());
        Log.d("CategoryAdapter", "onBindViewHolder: pos: " + position);
        holder.tv_notif.setText(notificationList.get(position).getMessage());
        holder.delete_notif.setOnClickListener(x->{
            db.collection("users")
                    .document(currUser.getUid())
                    .collection("notifications")
                    .document(notificationList.get(position).getId())
                    .delete();
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView tv_notif;
        Button delete_notif;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_notif = itemView.findViewById(R.id.tv_notification_message);
            delete_notif = itemView.findViewById(R.id.delete_notif);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if (dialog != null){
//                        dialog.onClick(getLayoutPosition());
//                    }
                }
            });
        }
    }
}
