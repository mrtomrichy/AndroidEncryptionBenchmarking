package com.mrtomrichy.encryptionbenchmarking.benchmarking;

import com.mrtomrichy.encryptionbenchmarking.encryption.Algorithm;

public class BenchmarkResult {
    public Algorithm algorithm;
    public boolean success;
    public String errorMessage;
    public int keySizeInBytes;
    public int sizeInBytes;
    public long timeTaken;

    public BenchmarkResult(Algorithm algorithm, boolean success, String errorMessage, int keySizeInBytes, int sizeInBytes, long timeTaken) {
        this.algorithm = algorithm;
        this.success = success;
        this.errorMessage = errorMessage;
        this.keySizeInBytes = keySizeInBytes;
        this.sizeInBytes = sizeInBytes;
        this.timeTaken = timeTaken;
    }
}
