package com.dandibhotla.ananth.wizardspacebattleapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ananth on 9/8/2017.
 */

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder>{

    private List<String> mSettings;
    private Context mContext;

    public SettingsAdapter(Context context, List<String> settings) {
        mSettings = settings;
        mContext = context;
    }

    private Context getContext() {
        return mContext;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView settingItemView;

        public ViewHolder(View itemView) {

            super(itemView);

            settingItemView = (TextView) itemView.findViewById(R.id.setting_title);

        }
    }
    @Override
    public SettingsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.setting_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(SettingsAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        String title = mSettings.get(position);

        // Set item views based on your views and data model
        TextView textView = viewHolder.settingItemView;
        textView.setText(title);

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mSettings.size();
    }

}
