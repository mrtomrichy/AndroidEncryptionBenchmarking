package com.mrtomrichy.encryptionbenchmarking;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mrtomrichy.encryptionbenchmarking.benchmarking.BenchmarkResult;

import java.util.ArrayList;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultsViewHolder> {

  ArrayList<BenchmarkResult> results;

  public ResultsAdapter(ArrayList<BenchmarkResult> results) {
    this.results = results;
  }

  @Override
  public ResultsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
    // create a new view
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
    BenchmarkResult item = results.get(position);

    resultsViewHolder.timeText.setText(item.timeTaken+"ms");
    resultsViewHolder.nameText.setText(item.algorithm.name);

    if(!item.success) {
      resultsViewHolder.timeText.setVisibility(View.INVISIBLE);
      resultsViewHolder.nameText.setTextColor(Color.parseColor("#F44336"));
    } else {
      resultsViewHolder.timeText.setVisibility(View.VISIBLE);
      resultsViewHolder.nameText.setTextColor(Color.parseColor("#757575"));
    }
  }

  public static class ResultsViewHolder extends RecyclerView.ViewHolder {
    public TextView nameText;
    public TextView timeText;
    public ResultsViewHolder(View itemView) {
      super(itemView);
      this.nameText = (TextView) itemView.findViewById(R.id.nameText);
      this.timeText = (TextView) itemView.findViewById(R.id.timeText);
    }
  }
}
