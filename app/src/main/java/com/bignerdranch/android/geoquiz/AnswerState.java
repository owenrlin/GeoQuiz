package com.bignerdranch.android.geoquiz;

import android.os.Parcel;
import android.os.Parcelable;

public class AnswerState implements Parcelable {

    private final int INDEX_ANSWERED = 0;
    private final int INDEX_CHEATED = 1;

    private boolean mAnswered;
    private boolean mCheated;

    public AnswerState() {
    };

    private AnswerState(Parcel in) {
        boolean[] answerState = in.createBooleanArray();
        mAnswered = answerState[INDEX_ANSWERED];
        mCheated = answerState[INDEX_CHEATED];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        boolean[] answerState = new boolean[]{false, false};
        answerState[INDEX_ANSWERED] = mAnswered;
        answerState[INDEX_CHEATED] = mCheated;
        dest.writeBooleanArray(answerState);
    }

    public boolean isAnswered() {
        return mAnswered;
    }

    public void setAnswered(boolean answered) {
        mAnswered = answered;
    }

    public boolean isCheated() {
        return mCheated;
    }

    public void setCheated(boolean cheated) {
        mCheated = cheated;
    }

    public static final Parcelable.Creator<AnswerState> CREATOR
            = new Parcelable.Creator<AnswerState>() {
        public AnswerState createFromParcel(Parcel in) {
            return new AnswerState(in);
        };

        @Override
        public AnswerState[] newArray(int size) {
            return new AnswerState[size];
        }
    };
};
