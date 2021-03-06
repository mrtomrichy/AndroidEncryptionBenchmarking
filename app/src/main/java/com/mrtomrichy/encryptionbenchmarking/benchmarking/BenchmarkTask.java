package com.mrtomrichy.encryptionbenchmarking.benchmarking;

import android.os.AsyncTask;

import com.mrtomrichy.encryptionbenchmarking.encryption.Algorithm;
import com.mrtomrichy.encryptionbenchmarking.encryption.Encryption;

public class BenchmarkTask extends AsyncTask<Algorithm, BenchmarkResult, Integer> {

    public interface BenchmarkCallbacks {
        void updateProgress(BenchmarkResult result);

        void benchmarkStarted();

        void benchmarkFinished(int successfulEncryptions);
    }

    private BenchmarkCallbacks callbacks;

    private byte[] data;
    private int dataSizeInBytes;
    private boolean stopped = false;

    public BenchmarkTask(byte[] data, BenchmarkCallbacks callbacks) {
        this.data = data;
        this.dataSizeInBytes = data.length / 8;

        this.callbacks = callbacks;
    }

    public void stop() {
        stopped = true;
    }

    @Override
    protected void onProgressUpdate(BenchmarkResult... progress) {
        if (this.callbacks != null) {
            callbacks.updateProgress(progress[0]);
        }
    }

    @Override
    protected Integer doInBackground(Algorithm... algorithms) {
        int successfulEncrypts = 0;
        for (Algorithm algorithm : algorithms) {
            if (stopped) return successfulEncrypts;

            String algorithmName = algorithm.name;

            long startTime = System.currentTimeMillis();
            boolean success = true;
            String errorMessage = null;
            try {
                Encryption.encrypt(this.data, algorithmName);
            } catch (Exception e) {
                success = false;
                errorMessage = e.getMessage();
            } finally {
                long timeTaken = -1;

                if (success) {
                    successfulEncrypts++;
                    timeTaken = System.currentTimeMillis() - startTime;
                }

                BenchmarkResult result = new BenchmarkResult(algorithm, success, errorMessage, Encryption.getKeySize(), dataSizeInBytes, timeTaken);
                publishProgress(result);
            }
        }

        return successfulEncrypts;
    }

    @Override
    protected void onPreExecute() {
        if (callbacks != null) {
            callbacks.benchmarkStarted();
        }
    }

    @Override
    protected void onPostExecute(Integer successfulEncryptions) {
        if (callbacks != null) {
            callbacks.benchmarkFinished(successfulEncryptions);
        }
    }
}
