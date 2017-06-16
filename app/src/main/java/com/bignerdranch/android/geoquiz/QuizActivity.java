package com.bignerdranch.android.geoquiz;

import android.content.Intent;
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

    private Button mCheatButton;
    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mPrevButton;
    private ImageButton mNextButton;
    private TextView mQuestionTextView;

    private Question[] mQuestionBank = new Question[] {
        new Question(R.string.question_australia, true),
        new Question(R.string.question_africa, false),
        new Question(R.string.question_americas, true),
        new Question(R.string.question_asia, true),
        new Question(R.string.question_mideast, false),
        new Question(R.string.question_oceans, true)
    };

    //Arrays are kinda hokey but will use to be consistent with book example
    private boolean[] mAnswerState = new boolean[] {false, false, false, false, false, false};
    private int mAnsweredCorrectly = 0;
    private int mAnswered = 0;

    private int mCurrentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(this.getClass().getSimpleName(), "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        if(savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX);
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
                startActivity(cheatIntent);
            }
        });

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
    }

    private void checkAnswer(boolean userAnswer) {

        mAnswerState[mCurrentIndex] = true;
        mAnswered++;

        int message = R.string.incorrect_toast;
        if(mQuestionBank[mCurrentIndex].isAnswerTrue() == userAnswer) {
            message = R.string.correct_toast;
            mAnsweredCorrectly++;
        }

        Toast.makeText(QuizActivity.this,
            message,
            Toast.LENGTH_SHORT).show();

        if(mAnswered == mQuestionBank.length) {
            String scoreFormat = getResources().getString(R.string.score_toast);
            float score = (float) mAnsweredCorrectly/mQuestionBank.length * 100;
            String scoreMessage = String.format(scoreFormat, score);
            Toast.makeText(QuizActivity.this,
                    scoreMessage,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void refreshButtons() {
        mTrueButton.setEnabled(!mAnswerState[mCurrentIndex]);
        mFalseButton.setEnabled(!mAnswerState[mCurrentIndex]);
    }

    private void updateQuestion() {
        int questionResId = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(questionResId);
    }
}
