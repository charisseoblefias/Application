package com.example.application.flashcardactivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.application.R;
import com.example.application.flashcarddatabase.FlashcardDatabase;

import java.util.List;
import java.util.Random;

public class FlashcardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard);

        // Initialize database
        flashcardDatabase = new FlashcardDatabase(getApplicationContext());
        allFlashcards = flashcardDatabase.getAllCards();

        if (allFlashcards != null && allFlashcards.size() > 0) {
            ((TextView) findViewById(R.id.flashcard_question)).setText(allFlashcards.get(0).getQuestion());
            ((TextView) findViewById(R.id.flashcard_answer)).setText(allFlashcards.get(0).getAnswer());
        }

        final TextView question =  (TextView) findViewById(R.id.flashcard_question);
        final TextView ans = (TextView) findViewById(R.id.flashcard_answer);
        final View background = findViewById(R.id.rootView);
        final ImageView addBtn = (ImageView) findViewById(R.id.addBtn);
        final ImageView deleteBtn = (ImageView) findViewById(R.id.deleteBtn);
        final ImageView nextBtn = (ImageView) findViewById(R.id.nextBtn);
        final ImageView prevBtn = (ImageView) findViewById(R.id.prevBtn);
        final TextView flashcardNum = (TextView) findViewById(R.id.flashcardNumber);

        flashcardNum.setText(currentCardDisplayedIndex+1 + " / " + allFlashcards.size() );

        if (allFlashcards.size() == 0) {
            question.setText("Add Card");
            ans.setText("No answers yet");
            nextBtn.setVisibility(View.GONE);
            prevBtn.setVisibility(View.GONE);
            flashcardNum.setVisibility(View.GONE);
        }

        if (allFlashcards.size() > 0) {
            flashcardNum.setVisibility(View.VISIBLE);
        }

        if (allFlashcards.size() == 1) {
            nextBtn.setVisibility(View.GONE);
            prevBtn.setVisibility(View.GONE);
        }

        question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (question.getVisibility() == View.VISIBLE) {
                    question.setVisibility(View.INVISIBLE);
                    ans.setVisibility(View.VISIBLE);

                }
                findViewById(R.id.flashcard_question).setCameraDistance(25000);
                findViewById(R.id.flashcard_answer).setCameraDistance(25000);
                question.animate()
                        .rotationY(90)
                        .setDuration(200)
                        .withEndAction(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        question.setVisibility(View.INVISIBLE);
                                        findViewById(R.id.flashcard_answer).setVisibility(View.VISIBLE);
                                        // second quarter turn
                                        findViewById(R.id.flashcard_answer).setRotationY(-90);
                                        findViewById(R.id.flashcard_answer).animate()
                                                .rotationY(0)
                                                .setDuration(200)
                                                .start();
                                    }
                                }
                        ).start();

            }
        });

        // See answer
        ans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ans.getVisibility() == View.VISIBLE) {
                    ans.setVisibility(View.INVISIBLE);
                    question.setVisibility(View.VISIBLE);
                }

                int cx = question.getWidth() / 2;
                int cy = question.getHeight() / 2;
                float finalRadius = (float) Math.hypot(cx, cy);

                // create the animator
                ans.animate()
                        .rotationY(-90)
                        .setDuration(200)
                        .withEndAction(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        ans.setVisibility(View.INVISIBLE);
                                        question.setVisibility(View.VISIBLE);
                                        question.setRotationY(90);
                                        question.animate()
                                                .rotationY(0)
                                                .setDuration(200)
                                                .start();

                                    }
                                }
                        )
                        .start();
            }
        });

        // Reset
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ans.setVisibility(View.INVISIBLE);
                question.setVisibility(View.VISIBLE);
            }
        });

        // Delete flashcard
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashcardDatabase.deleteCard(question.getText().toString());
                allFlashcards = flashcardDatabase.getAllCards();

                if (currentCardDisplayedIndex > 0) {
                    currentCardDisplayedIndex--;
                } else {
                    currentCardDisplayedIndex++;
                }
                if (currentCardDisplayedIndex > allFlashcards.size() - 1) {
                    currentCardDisplayedIndex = 0;
                }

                if (allFlashcards.size() > 0) {
                    question.setText(allFlashcards.get(currentCardDisplayedIndex).getQuestion());
                    ans.setText(allFlashcards.get(currentCardDisplayedIndex).getAnswer());
                    flashcardNum.setText(currentCardDisplayedIndex + 1 + " / " + allFlashcards.size());
                } else {
                    question.setText("Add a card!");
                    ans.setText("No answers yet!");
                    nextBtn.setVisibility(View.GONE);
                    prevBtn.setVisibility(View.GONE);
                    flashcardNum.setVisibility(View.GONE);
                }
                if (allFlashcards.size() == 1) {
                    nextBtn.setVisibility(View.GONE);
                    prevBtn.setVisibility(View.GONE);
                }
            }
        });

        // Add Flashcard
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FlashcardActivity.this, AddCardActivity.class);
                FlashcardActivity.this.startActivityForResult(intent, 100);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);


                if (allFlashcards.size() > 0) {
                    flashcardNum.setVisibility(View.VISIBLE);
                }

                if (allFlashcards.size() > 1) {
                    nextBtn.setVisibility(View.VISIBLE);
                    prevBtn.setVisibility(View.VISIBLE);
                }


            }

        });

        //Next Flashcard
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    // add animation
                    final Animation leftOutAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.slide_out_left);
                    final Animation rightInAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.slide_in_right);
                    leftOutAnim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            question.startAnimation(rightInAnim);
                            if (ans.getVisibility() == View.VISIBLE) {
                                ans.startAnimation(rightInAnim);
                            }
                            question.setText(allFlashcards.get(currentCardDisplayedIndex).getQuestion());
                            ans.setText(allFlashcards.get(currentCardDisplayedIndex).getAnswer());


                            flashcardNum.setText(currentCardDisplayedIndex + 1 + " / " + allFlashcards.size());

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });

                if (allFlashcards.size() > 2) {
                        // make next card random
                        Random rand = new Random();
                        int nextCard = rand.nextInt(allFlashcards.size());
                        if (nextCard == currentCardDisplayedIndex) {
                            nextCard = rand.nextInt(allFlashcards.size());
                        }
                        currentCardDisplayedIndex = nextCard;
                } else if (allFlashcards.size() <= 1){
                        nextBtn.setVisibility(View.GONE);
                        prevBtn.setVisibility(View.GONE);
                        currentCardDisplayedIndex = 0;
                } else {
                        currentCardDisplayedIndex++;
                        if (currentCardDisplayedIndex+1 > allFlashcards.size()) {
                        currentCardDisplayedIndex = 0;
                    }
                }
                    question.startAnimation(leftOutAnim);
                if (ans.getVisibility() == View.VISIBLE) {
                    ans.startAnimation(leftOutAnim);
                }

            }
        });

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Animation leftInAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.slide_in_left);
                final Animation rightOutAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.slide_out_right);
                rightOutAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        question.startAnimation(leftInAnim);
                        if (ans.getVisibility() == View.VISIBLE) {
                            ans.startAnimation(leftInAnim);
                        }
                        // Question and Answer from the database
                        ((TextView) findViewById(R.id.flashcard_question)).setText(allFlashcards.get(currentCardDisplayedIndex).getQuestion());
                        ((TextView) findViewById(R.id.flashcard_answer)).setText(allFlashcards.get(currentCardDisplayedIndex).getAnswer());
                        flashcardNum.setText(currentCardDisplayedIndex+1 + " / " + allFlashcards.size() );
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                currentCardDisplayedIndex--;
                if (currentCardDisplayedIndex < 0) {
                    currentCardDisplayedIndex = allFlashcards.size() - 1;
                }

                question.startAnimation(rightOutAnim);
                if (ans.getVisibility() == View.VISIBLE) {
                    ans.startAnimation(rightOutAnim);
                }
            }
        });
    }

    // Get data from Add Card Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            String question = data.getExtras().getString("string1");
            String answer = data.getExtras().getString("string2");
            ((TextView) findViewById(R.id.flashcard_question)).setText(question);
            ((TextView) findViewById(R.id.flashcard_answer)).setText(answer);

            flashcardDatabase.insertCard(new Flashcard(question, answer));
            allFlashcards = flashcardDatabase.getAllCards();

            if (allFlashcards.size() > 1) {
                findViewById(R.id.nextBtn).setVisibility(View.VISIBLE);
                findViewById(R.id.prevBtn).setVisibility(View.VISIBLE);
            }
            // Update flashcard number
            ((TextView) findViewById(R.id.flashcardNumber)).setText(allFlashcards.size() + " / " + allFlashcards.size());
            findViewById(R.id.flashcardNumber).setVisibility(View.VISIBLE);

        } else if (requestCode == 200 && resultCode == RESULT_OK) {
            String question = data.getExtras().getString("string1");
            String answer = data.getExtras().getString("string2");
            ((TextView) findViewById(R.id.flashcard_question)).setText(question);
            ((TextView) findViewById(R.id.flashcard_answer)).setText(answer);

            cardToEdit.setQuestion(question);
            cardToEdit.setAnswer(answer);
            flashcardDatabase.updateCard(cardToEdit);
        }
    }
    
    Flashcard cardToEdit;
    
    FlashcardDatabase flashcardDatabase;
    List<Flashcard> allFlashcards;

    int currentCardDisplayedIndex = 0;
}
