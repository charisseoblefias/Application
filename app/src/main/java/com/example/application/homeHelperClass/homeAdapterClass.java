package com.example.application.homeHelperClass;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.R;

import java.util.ArrayList;


public class homeAdapterClass extends RecyclerView.Adapter<homeAdapterClass.HomeViewHold>  {

    ArrayList<homehelper> homeLocations;
    final private ListItemClickListener mOnClickListener;

    public homeAdapterClass(ArrayList<homehelper> homeLocations, ListItemClickListener listener) {
        this.homeLocations = homeLocations;
        mOnClickListener = listener;
    }

    @NonNull

    @Override
    public HomeViewHold onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_recyclerview, parent, false);
        return new HomeViewHold(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHold holder, int position) {


       homehelper homehelper = homeLocations.get(position);
        holder.title.setText(homehelper.getTitle());
        holder.relativeLayout.setBackground(homehelper.getgradient());
    }

    @Override
    public int getItemCount() {
        return homeLocations.size();

    }

    public interface ListItemClickListener {
        void onphoneListClick(int clickedItemIndex);
    }

    public class HomeViewHold extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;
        RelativeLayout relativeLayout;


        public HomeViewHold(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            //hooks

            title = itemView.findViewById(R.id.card_title);
            relativeLayout = itemView.findViewById(R.id.background_color);

        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onphoneListClick(clickedPosition);
        }
    }

}