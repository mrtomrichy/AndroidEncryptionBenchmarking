package com.mrtomrichy.encryptionbenchmarking.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mrtomrichy.encryptionbenchmarking.R;
import com.mrtomrichy.encryptionbenchmarking.benchmarking.BenchmarkResult;

import java.util.ArrayList;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultsViewHolder> {

    private ArrayList<BenchmarkResult> results;
    private Context context;


    public ResultsAdapter(ArrayList<BenchmarkResult> results, Context context) {
        this.results = results;
        this.context = context;
    }

    @Override
    public ResultsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.result_layout, viewGroup, false);

        ResultsViewHolder vh = new ResultsViewHolder(v);
        return vh;
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    @Override
    public void onBindViewHolder(ResultsViewHolder resultsViewHolder, int position) {
        final BenchmarkResult item = results.get(position);

        resultsViewHolder.nameText.setText(item.algorithm.name);

        if (!item.success) {
            resultsViewHolder.nameText.setTextColor(context.getResources().getColor(R.color.failed_red));
            resultsViewHolder.timeText.setTextColor(context.getResources().getColor(R.color.failed_red));
            resultsViewHolder.timeText.setText(R.string.failed);
            resultsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showErrorDialog(item.errorMessage);
                }
            });
        } else {
            resultsViewHolder.nameText.setTextColor(context.getResources().getColor(android.R.color.secondary_text_dark));
            resultsViewHolder.timeText.setTextColor(context.getResources().getColor(android.R.color.secondary_text_dark));
            resultsViewHolder.timeText.setText(String.format(context.getString(R.string.time_taken), item.timeTaken));
            resultsViewHolder.timeText.setOnClickListener(null);
        }
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }

    class ResultsViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView timeText;

        ResultsViewHolder(View itemView) {
            super(itemView);
            this.nameText = (TextView) itemView.findViewById(R.id.nameText);
            this.timeText = (TextView) itemView.findViewById(R.id.timeText);
        }
    }
}
