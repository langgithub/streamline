package com.lang.streamline.utils;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ParelceText implements Parcelable {

    protected ParelceText(Parcel in) {
    }

    public static final Creator<ParelceText> CREATOR = new Creator<ParelceText>() {
        @Override
        public ParelceText createFromParcel(Parcel in) {
            return new ParelceText(in);
        }

        @Override
        public ParelceText[] newArray(int size) {
            return new ParelceText[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
    }
}
