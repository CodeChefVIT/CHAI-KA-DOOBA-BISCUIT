package partlyapp.techpeg.com.partly.Downloads;

import android.os.Parcel;
import android.os.Parcelable;

public class Progress implements Parcelable {

  public Progress() {
  }

  private int progress;
  private long currentFileSize;
  private long totalFileSize;

  public int getProgress() {
    return progress;
  }

  public void setProgress(int progress) {
    this.progress = progress;
  }

  public long getCurrentFileSize() {
    return currentFileSize;
  }

  public void setCurrentFileSize(long currentFileSize) {
    this.currentFileSize = currentFileSize;
  }

  public long getTotalFileSize() {
    return totalFileSize;
  }

  public void setTotalFileSize(long totalFileSize) {
    this.totalFileSize = totalFileSize;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(progress);
    dest.writeLong(currentFileSize);
    dest.writeLong(totalFileSize);
  }


  private Progress(Parcel in) {

    progress = in.readInt();
    currentFileSize = in.readLong();
    totalFileSize = in.readLong();
  }

  public static final Creator<Progress> CREATOR = new Creator<Progress>() {
    public Progress createFromParcel(Parcel in) {
      return new Progress(in);
    }

    public Progress[] newArray(int size) {
      return new Progress[size];
    }
  };
}