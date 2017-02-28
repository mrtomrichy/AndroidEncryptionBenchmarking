package com.mrtomrichy.encryptionbenchmarking.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mrtomrichy.encryptionbenchmarking.R;
import com.mrtomrichy.encryptionbenchmarking.encryption.Algorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tom on 04/11/2015.
 */
public class AlgorithmAdapter extends RecyclerView.Adapter<AlgorithmAdapter.ViewHolder> {

    public interface AlgorithmSelectionListener {
        void onAlgorithmPressed(AlgorithmListModel item);

        void onAlgorithmLongPressed(AlgorithmListModel item);
    }

    private ArrayList<AlgorithmListModel> results;
    private AlgorithmSelectionListener listener = null;
    private Context context;

    public AlgorithmAdapter(ArrayList<AlgorithmListModel> results, Context c) {
        this.results = results;
        this.context = c;
    }

    public void setAlgorithmSelectionListener(AlgorithmSelectionListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.algorithm_layout, viewGroup, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final AlgorithmListModel item = results.get(position);

        viewHolder.nameText.setText(item.algorithm.name);

        if (item.selected) {
            viewHolder.nameText.setTextColor(context.getResources().getColor(R.color.accent));
        } else {
            viewHolder.nameText.setTextColor(Color.WHITE);
        }
    }

    public int getSelectedCount() {
        int count = 0;

        for (AlgorithmListModel a : results) {
            if (a.selected) count++;
        }

        return count;
    }

    public List<Algorithm> getSelectedAlgorithms() {
        List<Algorithm> selectedAlgorithms = new ArrayList<>();
        for (AlgorithmAdapter.AlgorithmListModel algo : results) {
            if (algo.selected) selectedAlgorithms.add(algo.algorithm);
        }

        return selectedAlgorithms;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;

        ViewHolder(View itemView) {
            super(itemView);
            this.nameText = (TextView) itemView.findViewById(R.id.algorithmNameText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onAlgorithmPressed(results.get(getAdapterPosition()));
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (listener != null) {
                        listener.onAlgorithmLongPressed(results.get(getAdapterPosition()));
                    }
                    return true;
                }
            });
        }
    }

    public static class AlgorithmListModel {
        public Algorithm algorithm;
        public boolean selected;

        public AlgorithmListModel(Algorithm algorithm) {
            this.algorithm = algorithm;
            this.selected = false;
        }
    }
}
