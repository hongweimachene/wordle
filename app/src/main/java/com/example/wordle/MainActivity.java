package com.example.wordle;

import static android.view.inputmethod.EditorInfo.IME_ACTION_NEXT;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//This is a remote dependency, written by Ali Muzzafar
//https://github.com/alphamu/PinEntryEditText
import com.alimuzaffar.lib.pin.PinEntryEditText;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This is the main activity for the Wordle Application
 * @author Hong Wei Chen
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //open dictionary from resources folder, get string array of all words
        InputStream ins = getResources().openRawResource(R.raw.dictionary);
        String[] dictofWords = new BufferedReader(new InputStreamReader(ins, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"))
                .toUpperCase()
                .split("\n");

        //randomly select a word from dictionary
        SecureRandom rand = new SecureRandom("nuts".getBytes(StandardCharsets.UTF_8));
        //the string 'chosen' will be the word players have to guess
        String chosen = dictofWords[rand.nextInt(dictofWords.length)];


        //create keyboard
        MyKeyboard keyboard = (MyKeyboard) findViewById(R.id.keyboard);
        TextView textView = (TextView) findViewById(R.id.textview);

        //create play again button
        Button playAgain = (Button) findViewById(R.id.restartbutton);

        //create The hint layouts for each row
        LinearLayout row1= (LinearLayout) findViewById(R.id.row1_hint);
        LinearLayout row2= (LinearLayout) findViewById(R.id.row2_hint);
        LinearLayout row3= (LinearLayout) findViewById(R.id.row3_hint);
        LinearLayout row4= (LinearLayout) findViewById(R.id.row4_hint);
        LinearLayout row5= (LinearLayout) findViewById(R.id.row5_hint);
        LinearLayout row6= (LinearLayout) findViewById(R.id.row6_hint);

        //create the input boxes that the player will type to, players have 6 guesses
        final PinEntryEditText pinEntry1 = (PinEntryEditText) findViewById(R.id.txt_pin_entry1);
        final PinEntryEditText pinEntry2 = (PinEntryEditText) findViewById(R.id.txt_pin_entry2);
        final PinEntryEditText pinEntry3 = (PinEntryEditText) findViewById(R.id.txt_pin_entry3);
        final PinEntryEditText pinEntry4 = (PinEntryEditText) findViewById(R.id.txt_pin_entry4);
        final PinEntryEditText pinEntry5 = (PinEntryEditText) findViewById(R.id.txt_pin_entry5);
        final PinEntryEditText pinEntry6 = (PinEntryEditText) findViewById(R.id.txt_pin_entry6);

        //The input boxes are not supposed to be editable or selectable
        disableEditText(pinEntry1);
        disableEditText(pinEntry2);
        disableEditText(pinEntry3);
        disableEditText(pinEntry4);
        disableEditText(pinEntry5);
        disableEditText(pinEntry6);

        //direct focus and establish input connection to the first row of guessing
        enableEditText(pinEntry1);
        pinEntry1.requestFocus();
        InputConnection ic = pinEntry1.onCreateInputConnection(new EditorInfo());
        keyboard.setInputConnection(ic);

        //add onClickListener for playAgain button
        playAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                triggerRebirth(MainActivity.this);
            }
        });

        //OnFocusChangeListener to establish an InputConnection when the PinEntryEditText receives focus, so the keyboard can enter letters
        pinEntry1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    InputConnection ic = pinEntry1.onCreateInputConnection(new EditorInfo());
                    keyboard.setInputConnection(ic);
                }
            }
        });

        //OnEditorActionListener to handle when the Enter key is pressed on the keyboard while the PinEntryEditText is in focus
        pinEntry1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                String currentString = pinEntry1.getText().toString().toUpperCase();
                //Enter key press check
                if (actionId == IME_ACTION_NEXT) {
                    //If the player makes the correct guess, end the game
                    if (currentString.equals(chosen)) {
                        Toast.makeText(MainActivity.this, "Correct! You got the word", Toast.LENGTH_SHORT).show();
                        pinEntry1.setVisibility(View.GONE);
                        pinEntry2.setVisibility(View.GONE);
                        pinEntry3.setVisibility(View.GONE);
                        pinEntry4.setVisibility(View.GONE);
                        pinEntry5.setVisibility(View.GONE);
                        pinEntry6.setVisibility(View.GONE);
                        row2.setVisibility(View.VISIBLE);
                        row3.setVisibility(View.VISIBLE);
                        row4.setVisibility(View.VISIBLE);
                        row5.setVisibility(View.VISIBLE);
                        row6.setVisibility(View.VISIBLE);
                        rowHintReveal(row1, keyboard, currentString, chosen);
                        disableEditText(pinEntry1);
                        pinEntry1.clearFocus();
                        keyboard.closeInputConnection();
                        playAgain.setVisibility(View.VISIBLE);
                        return true;
                    }
                    //If the player makes an incorrect, update the activity to display hints, and set focus to the next PinEntryEditText
                    if (currentString.length() == chosen.length()) {
                        if (Arrays.asList(dictofWords).contains(currentString)) {
                            pinEntry1.setVisibility(View.GONE);
                            rowHintReveal(row1, keyboard, currentString, chosen);
                            disableEditText(pinEntry1);
                            enableEditText(pinEntry2);
                            pinEntry2.requestFocus();
                        } else {
                            //If the word is not a valid word in the list of words, notify player
                            Toast.makeText(MainActivity.this, "The word is not valid!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //Input must be a five-letter word
                        Toast.makeText(MainActivity.this, "The word is too short!", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });

        pinEntry2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    InputConnection ic = pinEntry2.onCreateInputConnection(new EditorInfo());
                    keyboard.setInputConnection(ic);
                }
            }
        });

        pinEntry2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                String currentString = pinEntry2.getText().toString().toUpperCase();
                if (actionId == IME_ACTION_NEXT) {
                    if (currentString.equals(chosen)) {
                        Toast.makeText(MainActivity.this, "Correct! You got the word", Toast.LENGTH_SHORT).show();
                        pinEntry2.setVisibility(View.GONE);
                        pinEntry3.setVisibility(View.GONE);
                        pinEntry4.setVisibility(View.GONE);
                        pinEntry5.setVisibility(View.GONE);
                        pinEntry6.setVisibility(View.GONE);
                        row3.setVisibility(View.VISIBLE);
                        row4.setVisibility(View.VISIBLE);
                        row5.setVisibility(View.VISIBLE);
                        row6.setVisibility(View.VISIBLE);
                        rowHintReveal(row2, keyboard, currentString, chosen);
                        disableEditText(pinEntry2);
                        pinEntry2.clearFocus();
                        keyboard.closeInputConnection();
                        playAgain.setVisibility(View.VISIBLE);
                        return true;
                    }
                    if (currentString.length() == chosen.length()) {
                        if (Arrays.asList(dictofWords).contains(currentString)) {
                            pinEntry2.setVisibility(View.GONE);
                            rowHintReveal(row2, keyboard, currentString, chosen);
                            disableEditText(pinEntry2);
                            enableEditText(pinEntry3);
                            pinEntry3.requestFocus();
                        } else {
                            Toast.makeText(MainActivity.this, "The word is not valid!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "The word is too short!", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });

        pinEntry3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    InputConnection ic = pinEntry3.onCreateInputConnection(new EditorInfo());
                    keyboard.setInputConnection(ic);
                }
            }
        });

        pinEntry3.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                String currentString = pinEntry3.getText().toString().toUpperCase();
                if (actionId == IME_ACTION_NEXT) {
                    if (currentString.equals(chosen)) {
                        Toast.makeText(MainActivity.this, "Correct! You got the word", Toast.LENGTH_SHORT).show();
                        pinEntry3.setVisibility(View.GONE);
                        pinEntry4.setVisibility(View.GONE);
                        pinEntry5.setVisibility(View.GONE);
                        pinEntry6.setVisibility(View.GONE);
                        row4.setVisibility(View.VISIBLE);
                        row5.setVisibility(View.VISIBLE);
                        row6.setVisibility(View.VISIBLE);
                        rowHintReveal(row3, keyboard, currentString, chosen);
                        disableEditText(pinEntry3);
                        pinEntry3.clearFocus();
                        keyboard.closeInputConnection();
                        playAgain.setVisibility(View.VISIBLE);
                        return true;
                    }
                    if (currentString.length() == chosen.length()) {
                        if (Arrays.asList(dictofWords).contains(currentString)) {
                            pinEntry3.setVisibility(View.GONE);
                            rowHintReveal(row3, keyboard, currentString, chosen);
                            disableEditText(pinEntry3);
                            enableEditText(pinEntry4);
                            pinEntry4.requestFocus();
                        } else {
                            Toast.makeText(MainActivity.this, "The word is not valid!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "The word is too short!", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });

        pinEntry4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    InputConnection ic = pinEntry4.onCreateInputConnection(new EditorInfo());
                    keyboard.setInputConnection(ic);
                }
            }
        });

        pinEntry4.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                String currentString = pinEntry4.getText().toString().toUpperCase();
                if (actionId == IME_ACTION_NEXT) {
                    if (currentString.equals(chosen)) {
                        Toast.makeText(MainActivity.this, "Correct! You got the word", Toast.LENGTH_SHORT).show();
                        pinEntry4.setVisibility(View.GONE);
                        pinEntry5.setVisibility(View.GONE);
                        pinEntry6.setVisibility(View.GONE);
                        row5.setVisibility(View.VISIBLE);
                        row6.setVisibility(View.VISIBLE);
                        rowHintReveal(row4, keyboard, currentString, chosen);
                        disableEditText(pinEntry4);
                        pinEntry4.clearFocus();
                        keyboard.closeInputConnection();
                        playAgain.setVisibility(View.VISIBLE);
                        return true;
                    }
                    if (currentString.length() == chosen.length()) {
                        if (Arrays.asList(dictofWords).contains(currentString)) {
                            pinEntry4.setVisibility(View.GONE);
                            rowHintReveal(row4, keyboard, currentString, chosen);
                            disableEditText(pinEntry4);
                            enableEditText(pinEntry5);
                            pinEntry5.requestFocus();
                        } else {
                            Toast.makeText(MainActivity.this, "The word is not valid!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "The word is too short!", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });

        pinEntry5.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    InputConnection ic = pinEntry5.onCreateInputConnection(new EditorInfo());
                    keyboard.setInputConnection(ic);
                }
            }
        });

        pinEntry5.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                String currentString = pinEntry5.getText().toString().toUpperCase();
                if (actionId == IME_ACTION_NEXT) {
                    if (currentString.equals(chosen)) {
                        Toast.makeText(MainActivity.this, "Correct! You got the word", Toast.LENGTH_SHORT).show();
                        pinEntry5.setVisibility(View.GONE);
                        pinEntry6.setVisibility(View.GONE);
                        row6.setVisibility(View.VISIBLE);
                        rowHintReveal(row5, keyboard, currentString, chosen);
                        disableEditText(pinEntry5);
                        pinEntry5.clearFocus();
                        keyboard.closeInputConnection();
                        playAgain.setVisibility(View.VISIBLE);
                        return true;
                    }
                    if (currentString.length() == chosen.length()) {
                        if (Arrays.asList(dictofWords).contains(currentString)) {
                            pinEntry5.setVisibility(View.GONE);
                            rowHintReveal(row5, keyboard, currentString, chosen);
                            disableEditText(pinEntry5);
                            enableEditText(pinEntry6);
                            pinEntry6.requestFocus();
                        } else {
                            Toast.makeText(MainActivity.this, "The word is not valid!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "The word is too short!", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });

        pinEntry6.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    InputConnection ic = pinEntry6.onCreateInputConnection(new EditorInfo());
                    keyboard.setInputConnection(ic);
                }
            }
        });

        pinEntry6.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                String currentString = pinEntry6.getText().toString().toUpperCase();
                if (actionId == IME_ACTION_NEXT) {
                    if (currentString.equals(chosen)) {
                        Toast.makeText(MainActivity.this, "Correct! You got the word", Toast.LENGTH_SHORT).show();
                        pinEntry6.setVisibility(View.GONE);
                        rowHintReveal(row6, keyboard, currentString, chosen);
                        disableEditText(pinEntry6);
                        pinEntry6.clearFocus();
                        keyboard.closeInputConnection();
                        playAgain.setVisibility(View.VISIBLE);
                        return true;
                    }
                    //If player has made 6 guesses, end the game and tell the player the correct word
                    if (currentString.length() == chosen.length()) {
                        if (Arrays.asList(dictofWords).contains(currentString)) {
                            Toast.makeText(MainActivity.this, "Game Over! The correct word was: " + chosen, Toast.LENGTH_LONG).show();
                            pinEntry6.setVisibility(View.GONE);
                            rowHintReveal(row6, keyboard, currentString, chosen);
                            disableEditText(pinEntry6);
                            if (ic != null) ic.closeConnection();
                            playAgain.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(MainActivity.this, "The word is not valid!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "The word is too short!", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });
    }


    /**
     * This method disables an EditText, preventing users from selecting it and entering input
     * @param editText the EditText to disable
     */
    private void disableEditText(EditText editText) {
        editText.setShowSoftInputOnFocus(false);
        editText.setTextIsSelectable(false);
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setFocusableInTouchMode(false);
    }

    /**
     * This method enables an EditText view, allowing it to be focusable
     * @param editText the EditText to enable
     */
    private void enableEditText(EditText editText){
        editText.setEnabled(true);
        editText.setFocusable(true);
        editText.setCursorVisible(true);
        editText.setFocusableInTouchMode(true);
    }

    /**
     * This method controls the hints given to a player after they have made a guess
     * @param layout the row of the  that is to be displayed
     * @param keyboard the keyboard
     * @param entered the player entered word
     * @param chosen the randomly generated word
     */
    private void rowHintReveal(LinearLayout layout, MyKeyboard keyboard, String entered, String chosen){
        //generate character array to check each letter
        char[] charsEntered = entered.toCharArray();
        char[] charsChosen = chosen.toCharArray();

        //counts the occurrence of each letter in the randomly generated word
        HashMap<Character, Integer> counts = new HashMap<Character, Integer>();
        for (char x : charsChosen){
            counts.put(x, counts.getOrDefault(x,0)+1);
        }

        //Letter check to give priority to letters that are in the correct position, consumes a count
        //so the yellow hint wont be triggered if there are repeated letters in the player guess
        for (int j = 0; j < charsEntered.length; j++){
            if (chosen.contains(Character.toString(charsEntered[j]))) {
                if (charsEntered[j] == charsChosen[j]){
                    counts.put(charsChosen[j], counts.get(charsChosen[j])-1);
                }
            }
        }

        //get the keyboard values
        HashMap<Integer, String> keyValues = keyboard.getKeyValues();

        //makes the hint layout visible
        layout.setVisibility(View.VISIBLE);

        //update each TextView's colors and the keyboard based on correctness of the guess
        for (int i = 0; i < layout.getChildCount(); i++){
            //string form of the character
            String s = String.valueOf(charsEntered[i]);

            TextView txtView = (TextView) layout.getChildAt(i);
            txtView.setText(s);
            txtView.setTextColor(Color.WHITE);
            txtView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            //find corresponding key to the letter
            Button key = findViewById(getKey(keyValues, s));

            //If the letter is in the word AND is in the correct position, color it green
            if (charsChosen[i] == charsEntered[i]) {
                txtView.setBackgroundResource(R.color.green);
                //update key if key has never been changed or it has a yellow hint already, since green hint overrides yellow
                if (key.getTag().equals("unchanged") || key.getTag().equals("marked")){
                    key.setTag("changed");
                    int color = Color.parseColor("#50C878");
                    key.getBackground().mutate().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC));
                }
            }

            //If the letter is in the word, color it yellow
            else if (chosen.contains(Character.toString(charsEntered[i])) && counts.get(charsEntered[i])>0) {
                txtView.setBackgroundResource(R.color.yellow);
                //consume a count so yellow hint wont be triggered again for repeated letter in player guess
                counts.put(charsEntered[i], counts.get(charsEntered[i])-1);
                if (key.getTag().equals("unchanged")){
                    key.setTag("marked");
                    int color = Color.parseColor("#FDDA0D");
                    key.getBackground().mutate().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC));
                }
            }

            //If the letter is not in the word, color it dark
            else {
                txtView.setBackgroundResource(R.color.charcoal);
                if (key.getTag().equals("unchanged")){
                    key.setTag("changed");
                    int color = Color.parseColor("#36454F");
                    key.getBackground().mutate().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC));
                }
            }
        }
    }

    /**
     * Get a key from a hashmap by its value
     * @param map the hashmap to observe
     * @param value the value to search by
     * @return the key of the corresponding value
     */
    public <Integer, String> Integer getKey(HashMap<Integer, String> map, String value){
        for (Map.Entry<Integer, String> entry: map.entrySet()){
            if (entry.getValue().equals(value)){
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Restart the app
     * @param context the app to restart
     */
    public static void triggerRebirth(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        context.startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }
}