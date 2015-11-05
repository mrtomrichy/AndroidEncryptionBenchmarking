package com.mrtomrichy.encryptionbenchmarking.encryption;

import android.os.Parcel;
import android.os.Parcelable;

public class Algorithm implements Parcelable {
  public String name;

  public Algorithm(String name) {
    this.name = name;
  }

  protected Algorithm(Parcel in) {
    name = in.readString();
  }


  public static final Creator<Algorithm> CREATOR = new Creator<Algorithm>() {
    @Override
    public Algorithm createFromParcel(Parcel in) {
      return new Algorithm(in);
    }

    @Override
    public Algorithm[] newArray(int size) {
      return new Algorithm[size];
    }
  };

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int i) {
    parcel.writeString(name);
  }
}
