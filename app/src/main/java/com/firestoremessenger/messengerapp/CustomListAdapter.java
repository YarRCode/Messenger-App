package com.firestoremessenger.messengerapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

public class CustomListAdapter extends BaseAdapter {

    Context context;

    String authorList[];
    String messageList[];

    LayoutInflater inflater;

    public CustomListAdapter(Context cnx, String[] author, String message[]) {
        this.context = cnx;
        this.authorList = author;
        this.messageList = message;

        inflater = LayoutInflater.from(cnx);
    }

    @Override
    public int getCount() {
        return messageList.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.activity_custom_list, null);

        TextView tvMessage = view.findViewById(R.id.tvMessage);
        TextView tvAuthor = view.findViewById(R.id.tvAuthor);

        tvMessage.setText(messageList[i]);
        tvAuthor.setText(authorList[i]);

        return view;
    }
}
