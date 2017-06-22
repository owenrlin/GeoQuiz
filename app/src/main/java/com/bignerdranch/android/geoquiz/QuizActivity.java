package com.bignerdranch.android.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private static final String KEY_INDEX = "index";
    private static final String KEY_ANSWERED_COUNT = "answered_count";
    private static final String KEY_ANSWERED_CORRECTLY_COUNT = "answered_correctly_count";
    private static final String KEY_IS_CHEATER = "is_cheater";
    private static final int REQUEST_CODE_CHEAT = 0;
    private static final int CHEAT_LIMIT = 3;

    private Button mCheatButton;
    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mPrevButton;
    private ImageButton mNextButton;
    private TextView mQuestionTextView;
    private TextView mRemainingCheatsTextView;


    private Question[] mQuestionBank = new Question[] {
        new Question(R.string.question_australia, true),
        new Question(R.string.question_africa, false),
        new Question(R.string.question_americas, true),
        new Question(R.string.question_asia, true),
        new Question(R.string.question_mideast, false),
        new Question(R.string.question_oceans, true)
    };

    private AnswerState[] mAnswerState = new AnswerState[] {
        new AnswerState(),
        new AnswerState(),
        new AnswerState(),
        new AnswerState(),
        new AnswerState(),
        new AnswerState()
    };

    private int mAnsweredCount = 0;
    private int mAnsweredCorrectlyCount = 0;
    private int mCurrentIndex = 0;
    private int mRemainingCheatCount = CHEAT_LIMIT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(this.getClass().getSimpleName(), "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        if(savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX);
            mAnsweredCount = savedInstanceState.getInt(KEY_ANSWERED_COUNT);
            mAnsweredCorrectlyCount = savedInstanceState.getInt(KEY_ANSWERED_CORRECTLY_COUNT);
            mAnswerState = (AnswerState[])savedInstanceState.getParcelableArray(KEY_IS_CHEATER);
        }

        mQuestionTextView = (TextView)findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (++mCurrentIndex) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mTrueButton = (Button)findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)  {
                checkAnswer(true);
                refreshButtons();
            }
           });

        mFalseButton = (Button)findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v)  {
                checkAnswer(false);
               refreshButtons();
           }
       });

        mCheatButton = (Button)findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)  {
                Intent cheatIntent = CheatActivity.newIntent(
                        QuizActivity.this,
                        mQuestionBank[mCurrentIndex].isAnswerTrue());
                startActivityForResult(cheatIntent, REQUEST_CODE_CHEAT);
            }
        });


        mRemainingCheatsTextView = (TextView)findViewById(R.id.remaining_cheats_text_view);
        updateRemainingCheats();

        mNextButton = (ImageButton)findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (++mCurrentIndex) % mQuestionBank.length;
                updateQuestion();
                refreshButtons();
            }
        });

        mPrevButton = (ImageButton)findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mQuestionBank.length + --mCurrentIndex) % mQuestionBank.length;
                updateQuestion();
                refreshButtons();
            }
        });

        updateQuestion();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(this.getClass().getSimpleName(), "onStart() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(this.getClass().getSimpleName(), "onResume() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(this.getClass().getSimpleName(), "onPause() called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(this.getClass().getSimpleName(), "onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(this.getClass().getSimpleName(), "onDestroy() called");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.d(this.getClass().getSimpleName(), "onSaveInstanceState() called");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putInt(KEY_ANSWERED_COUNT, mAnsweredCount);
        savedInstanceState.putInt(KEY_ANSWERED_CORRECTLY_COUNT, mAnsweredCorrectlyCount);
        savedInstanceState.putParcelableArray(KEY_IS_CHEATER, mAnswerState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        if(resultCode != Activity.RESULT_OK) { return; }

        if(requestCode == REQUEST_CODE_CHEAT) {
            if((result != null) && (CheatActivity.wasAnswerShown(result))) {
                mAnswerState[mCurrentIndex].setCheated(true);
                mRemainingCheatCount--;
                updateRemainingCheats();

                if(mRemainingCheatCount == 0) {
                    mCheatButton.setEnabled(false);
                }
            }
        }
    }

    private void checkAnswer(boolean userAnswer) {

        mAnswerState[mCurrentIndex].setAnswered(true);
        mAnsweredCount++;

        int message = R.string.incorrect_toast;
        if(mAnswerState[mCurrentIndex].isCheated()) {
            message = R.string.judgment_toast;
        }
        else if(mQuestionBank[mCurrentIndex].isAnswerTrue() == userAnswer) {
            message = R.string.correct_toast;
            mAnsweredCorrectlyCount++;
        }

        Toast.makeText(QuizActivity.this,
            message,
            Toast.LENGTH_SHORT).show();

        if(mAnsweredCount == mQuestionBank.length) {
            String scoreFormat = getResources().getString(R.string.score_toast);
            float score = (float) mAnsweredCorrectlyCount /mQuestionBank.length * 100;
            String scoreMessage = String.format(scoreFormat, score);
            Toast.makeText(QuizActivity.this,
                    scoreMessage,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void refreshButtons() {
        mTrueButton.setEnabled(!mAnswerState[mCurrentIndex].isAnswered());
        mFalseButton.setEnabled(!mAnswerState[mCurrentIndex].isAnswered());
    }

    private void updateQuestion() {
        int questionResId = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(questionResId);
    }

    private void updateRemainingCheats() {
        String remainingCheatFormat = getResources().getString(R.string.remaining_cheats_text);
        String remainingCheatMessage = String.format(remainingCheatFormat, mRemainingCheatCount);
        mRemainingCheatsTextView.setText(remainingCheatMessage);
    }
}
