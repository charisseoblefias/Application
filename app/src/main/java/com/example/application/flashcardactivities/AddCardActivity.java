package com.example.application.flashcardactivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.application.R;

public class AddCardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        final ImageView cancelBtn = (ImageView) findViewById(R.id.cancelBtn);
        final ImageView saveBtn = (ImageView) findViewById(R.id.saveBtn);
        final EditText question = (EditText) findViewById(R.id.question);
        final EditText answer = (EditText) findViewById(R.id.answer);

        // Cancel then go to Flashcard Activity
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddCardActivity.this, FlashcardActivity.class);
                AddCardActivity.this.startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        // Save Button then go back to Flashcard Activity
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get data
                String questionStr = question.getText().toString();
                String answerStr = answer.getText().toString();


                Intent data = new Intent();
                data.putExtra("string1", questionStr);
                data.putExtra("string2", answerStr);
                setResult(RESULT_OK, data);
                finish();
            }
        });

    }

    // Get data from Flashcard Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            String current_question = getIntent().getStringExtra("question");
            String current_ans = getIntent().getStringExtra("answer");
            ((EditText) findViewById(R.id.question)).setText(current_question);
            ((EditText) findViewById(R.id.answer)).setText(current_ans);

        }
    }

    // Exit animation

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
