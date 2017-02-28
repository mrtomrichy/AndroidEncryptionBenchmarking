package com.mrtomrichy.encryptionbenchmarking.ui;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mrtomrichy.encryptionbenchmarking.R;
import com.mrtomrichy.encryptionbenchmarking.adapter.ResultsAdapter;
import com.mrtomrichy.encryptionbenchmarking.benchmarking.BenchmarkResult;
import com.mrtomrichy.encryptionbenchmarking.benchmarking.BenchmarkTask;
import com.mrtomrichy.encryptionbenchmarking.encryption.Algorithm;
import com.mrtomrichy.encryptionbenchmarking.encryption.Encryption;

import java.util.ArrayList;

/**
 * Created by tom on 04/11/2015.
 */
public class BenchmarkActivity extends AppCompatActivity {

  BenchmarkTask.BenchmarkCallbacks callbacks = new BenchmarkTask.BenchmarkCallbacks() {
    @Override
    public void updateProgress(BenchmarkResult result) {
      results.add(result);
      mAdapter.notifyDataSetChanged();
      mProgressBar.setProgress(results.size());
    }

    @Override
    public void benchmarkStarted() {
      runButton.setText("Stop");
      keySizeInput.setEnabled(false);
      dataSizeInput.setEnabled(false);
      mResultsText.setText("");
      mProgressBar.setProgress(0);
    }

    @Override
    public void benchmarkFinished(int successfulEncryptions) {
      runButton.setText("Run");
      runButton.setEnabled(true);
      keySizeInput.setEnabled(true);
      dataSizeInput.setEnabled(true);
      currentTask = null;
      mResultsText.setText(successfulEncryptions + "/" + results.size() + " successful encryptions");
    }
  };

  private Button runButton;
  private EditText keySizeInput;
  private EditText dataSizeInput;
  private ResultsAdapter mAdapter;
  private TextView mResultsText;
  private ProgressBar mProgressBar;

  private ArrayList<BenchmarkResult> results = new ArrayList<>();

  private BenchmarkTask currentTask = null;

  private Algorithm[] selectedAlgorithms;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_benchmark);

    if(getIntent().getExtras().containsKey("algorithms")) {
      Parcelable[] parcelableArray = getIntent().getExtras().getParcelableArray("algorithms");
      selectedAlgorithms = new Algorithm[parcelableArray.length];
      System.arraycopy(parcelableArray, 0, selectedAlgorithms, 0, parcelableArray.length);
    } else {
      finish();
    }

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("Benchmark");
    if(selectedAlgorithms.length != 1) {
      getSupportActionBar().setSubtitle(selectedAlgorithms.length + " algorithms selected");
    } else {
      getSupportActionBar().setSubtitle(selectedAlgorithms[0].name);
    }

    keySizeInput = (EditText) findViewById(R.id.keySize);
    dataSizeInput = (EditText) findViewById(R.id.dataSize);
    mResultsText = (TextView) findViewById(R.id.resultText);
    mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

    runButton = (Button) findViewById(R.id.runButton);
    runButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if(currentTask == null){
          runBenchmarks();
        } else {
          currentTask.stop();
          runButton.setEnabled(false);
        }
      }
    });

    keySizeInput.setText("16");
    dataSizeInput.setText("100000");


    RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.resultsList);
    mRecyclerView.setHasFixedSize(true);

    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
    mRecyclerView.setLayoutManager(mLayoutManager);

    mAdapter = new ResultsAdapter(results, this);
    mRecyclerView.setAdapter(mAdapter);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void runBenchmarks() {
    String keySizeText = keySizeInput.getText().toString();
    String dataSizeText = dataSizeInput.getText().toString();

    if(keySizeText.isEmpty() || dataSizeText.isEmpty()) {
      return;
    }

    int keySize;
    int dataSize;

    try {
      keySize = Integer.parseInt(keySizeText);
      dataSize = Integer.parseInt(dataSizeText);
    } catch(NumberFormatException ex) {
      return;
    }

    results.clear();
    mAdapter.notifyDataSetChanged();

    mProgressBar.setMax(selectedAlgorithms.length);

    Encryption.setKeySize(keySize);

    byte[] data = Encryption.getRandomByteArray(dataSize);

    currentTask = new BenchmarkTask(data, callbacks);

    currentTask.execute(selectedAlgorithms);
  }
}
