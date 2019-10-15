package wifidirect.wifidirect.Message;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import wifidirect.wifidirect.R;

public class MessageAdapter extends BaseAdapter {

    private List<Message> messages = new ArrayList<Message>();
    Context context;

    public MessageAdapter(Context context) {
        this.context = context;
    }

    /**
     * Use this method to add received or sent message in list.
     * @param message a message which is received or sent
     */
    public void add(Message message) {
        this.messages.add(message);
        notifyDataSetChanged(); // to render the list we need to notify
    }

    /**
     * Return the number of messages in list.
     * @return the number of messages in list.
     */
    @Override
    public int getCount() {
        return messages.size();
    }

    /**
     * Return ith item in list.
     * @param i indicate index of message list.
     * @return ith item in list.
     */
    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * This is the backbone of the class.
     * It handles the creation of single ListView row (chat bubble).
     *
     * @param i The position of the item within the adapter's data set of the item whose view we want.
     * @param convertView The old view to reuse, if possible.
     * @param viewGroup The parent that this view will eventually be attached to.
     * @return 	A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        MessageViewHolder holder = new MessageViewHolder();
        LayoutInflater messageInflater =
                (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        Message message = messages.get(i);

        if (message.isBelongsToCurrentUser()) {
            convertView = messageInflater.inflate(R.layout.my_message, null);
            holder.messageBody = convertView.findViewById(R.id.message_body);
            convertView.setTag(holder);
            holder.messageBody.setText(message.getText());
        } else {
            convertView = messageInflater.inflate(R.layout.their_message, null);
            holder.avatar = convertView.findViewById(R.id.avatar);
            holder.name = convertView.findViewById(R.id.name);
            holder.messageBody = convertView.findViewById(R.id.message_body);
            convertView.setTag(holder);

            holder.messageBody.setText(message.getText());
            holder.name.setText(message.getSenderName());
        }

        return convertView;
    }
}
