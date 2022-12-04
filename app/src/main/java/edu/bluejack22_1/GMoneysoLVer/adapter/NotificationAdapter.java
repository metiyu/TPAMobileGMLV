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

import edu.bluejack22_1.GMoneysoLVer.R;

import edu.bluejack22_1.GMoneysoLVer.model.Notification;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationViewHolder> {
    private Context c;
    List<Notification> notificationList;
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
        return new NotificationViewHolder(view).linkAdapter(this);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
      /*  Log.d("onBindViewHolder", "onBindViewHolder: " + c.getClass().getName());
        Log.d("CategoryAdapter", "onBindViewHolder: pos: " + position);*/
        holder.tv_notif.setText(c.getString(R.string.message));
        holder.delete_notif.setOnClickListener(x->{
            db.collection("users")
                    .document(currUser.getUid())
                    .collection("notifications")
                    .document(notificationList.get(position).getId())
                    .delete().addOnCompleteListener(
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            holder.delete_notif(position);
                        }
                    }
            );
        });
    }

//    @Override
//    public void onBindViewHolder(@NonNull NotificationAdapter.NotificationViewHolder holder, int position) {
//        Log.d("onBindViewHolder", "onBindViewHolder: " + c.getClass().getName());
//        Log.d("CategoryAdapter", "onBindViewHolder: pos: " + position);
//        holder.tv_notif.setText(notificationList.get(position).getMessage());
//        holder.delete_notif.setOnClickListener(x->{
//            db.collection("users")
//                    .document(currUser.getUid())
//                    .collection("notifications")
//                    .document(notificationList.get(position).getId())
//                    .delete();
//        });
//    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

}
class NotificationViewHolder extends RecyclerView.ViewHolder {
    TextView tv_notif;
    Button delete_notif;
    NotificationAdapter adapter;

    public NotificationViewHolder(@NonNull View itemView) {
        super(itemView);
        tv_notif = itemView.findViewById(R.id.tv_notification_message);
        delete_notif = itemView.findViewById(R.id.delete_notif);
        itemView.findViewById(R.id.delete_notif).setOnClickListener(view->{
//            notificationList.clear();
            adapter.notificationList.remove(getAdapterPosition());
            adapter.notifyItemRemoved(getAdapterPosition());
        });
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
////                    if (dialog != null){
////                        dialog.onClick(getLayoutPosition());
////                    }
//                }
//            });
    }
    public NotificationViewHolder linkAdapter(NotificationAdapter adapter){
        this.adapter= adapter;
        return this;
    }
    public void delete_notif(int position){
        adapter.notificationList.remove(position);
        adapter.notifyItemRemoved(position);
    }
}
