package com.notes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.choota.dev.ctimeago.TimeAgo;
import com.makeramen.roundedimageview.RoundedImageView;
import com.notes.R;
import com.notes.model.Note;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    public ArrayList<Note> mData;
    public View.OnClickListener onItemClickListener;

    public int[] dots = {R.drawable.ic_cyan, R.drawable.ic_orange_accent,
            R.drawable.ic_pink, R.drawable.ic_purple, R.drawable.gradient_sub_1, R.drawable.gradient_sub_2,
            R.drawable.gradient_sub_3, R.drawable.gradient_sub_4, R.drawable.gradient_sub_5
    };

    public Context context;
    public NotesAdapter(Context context, ArrayList<Note> mData) {
        this.context = context;
        this.mData = mData;
    }

    @NonNull
    @Override
    public NotesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.note_model, parent, false);
        return new NotesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.ViewHolder holder, int position) {
        final int random = new Random().nextInt(9);
        holder.dot.setImageDrawable(holder.itemView.getResources().getDrawable(dots[random]));
        holder.text.setText(mData.get(position).note);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("IST"));
        try {
            Date d = sdf.parse(mData.get(position).timestamp);
            String dt = new TimeAgo().getTimeAgo(d);
            holder.timestamp.setText(dt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView dot;
        TextView text, timestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dot = itemView.findViewById(R.id.dot);
            text = itemView.findViewById(R.id.note);
            timestamp = itemView.findViewById(R.id.timestamp);
            itemView.setTag(this);
            itemView.setOnClickListener(onItemClickListener);
        }
    }

    public void setItemClickListener(View.OnClickListener clickListener) {
        onItemClickListener = clickListener;
    }
}
