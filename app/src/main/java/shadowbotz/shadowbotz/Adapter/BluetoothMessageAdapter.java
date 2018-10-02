package shadowbotz.shadowbotz.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import shadowbotz.shadowbotz.Model.BluetoothMessage;
import shadowbotz.shadowbotz.R;

public class BluetoothMessageAdapter extends RecyclerView.Adapter<BluetoothMessageAdapter.ChatViewHolder> {

    private String currentDeviceAddress = "";
    private ArrayList<BluetoothMessage> BluetoothMessageList;

    private LayoutInflater mInflater;
    private ListViewListener mListViewListener;

    public BluetoothMessageAdapter(Context context, ArrayList<BluetoothMessage> BluetoothMessageList, ListViewListener listViewListener) {
        mInflater = LayoutInflater.from(context);
        this.BluetoothMessageList = BluetoothMessageList;
        mListViewListener = listViewListener;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.message_bubble, parent, false);

        return new ChatViewHolder(view, mListViewListener);
    }

    @Override
    public void onBindViewHolder(BluetoothMessageAdapter.ChatViewHolder holder, int position) {
        BluetoothMessage BluetoothMessage = BluetoothMessageList.get(position);

        String sender = BluetoothMessage.getDeviceName();
        holder.textViewSender.setText(sender);

        holder.textViewMessage.setText(BluetoothMessage.getMessage());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy h:mm a", Locale.ENGLISH);
        String formattedDate = dateFormat.format(BluetoothMessage.getDatetime());

        holder.textViewTime.setText(formattedDate);

        if(BluetoothMessage.getDeviceAddress().equals(currentDeviceAddress)) {
            holder.layoutForMessage.setGravity(Gravity.END);
        }
        else {
            holder.layoutForMessage.setGravity(Gravity.START);
        }
    }

    @Override
    public int getItemCount() {
        return BluetoothMessageList.size();
    }

    public void update(ArrayList<BluetoothMessage> BluetoothMessageList) {
        this.BluetoothMessageList = BluetoothMessageList;
        notifyDataSetChanged();
    }

    public void setCurrentDeviceAddress(String currentDeviceAddress) {
        this.currentDeviceAddress = currentDeviceAddress;
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewSender, textViewMessage, textViewTime;
        LinearLayout layoutForMessage;
        ListViewListener mListViewListener;

        public ChatViewHolder(View itemView, ListViewListener listViewListener) {
            super(itemView);

            itemView.setOnClickListener(this);

            textViewSender = itemView.findViewById(R.id.textViewSender);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            layoutForMessage = itemView.findViewById(R.id.layoutForMessage);

            mListViewListener = listViewListener;
        }

        @Override
        public void onClick(View v) {
            mListViewListener.onSelected(getLayoutPosition());
        }
    }
}