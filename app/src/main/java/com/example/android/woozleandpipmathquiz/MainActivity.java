/*
 * No Copyright at all(NC) 2017
 *
 * Not Licensed under what ever License, Version 3.0 (the "No License");
 * you may  use this file.
 * You may also obtain a copy as many as you want
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the "No License" is distributed on an "AS IS" BASIS,
 * WITH WARRANTIES OR CONDITIONS OF SPECIFIC KIND, either express or implied.
 * See NO License for the general language governing permissions and
 * limitations or what ever.
 */
package com.example.android.woozleandpipmathquiz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.example.android.woozleandpipmathquiz.R;

import java.util.Random;
import static android.R.color.holo_red_dark;
import static android.R.id.message;
import static com.example.android.woozleandpipmathquiz.R.drawable.sun;
import static com.example.android.woozleandpipmathquiz.R.id.editTextAnswer;
import static com.example.android.woozleandpipmathquiz.R.drawable.cloud;

/**
 * This app displays a Math quiz (high school level) with a Woezel and Pip theme. (Woozle & Pip)
 * different methods are use between level 0-5 and the other levels
 */
public class MainActivity extends AppCompatActivity {
    // public constants used to get the code more readable
    public static final int DEFAULT_DIGIT_VALUE = 0;
    public static final int MAXIMUM_DIGIT_VALUES = 9;
    public static final boolean DEFAULT_BOOLEAN_VALUE = false;
    public static final int VISIBLE = 1;
    public static final int INVISIBLE = 0;
    public static final int DURATION_BLINKING_BUTTONS = 250;
    public static final int MAXIMUM_CALC_LEVEL = 9;

    // Constants to construct the calculation strings
    public static final String ADD = "+";
    public static final String SUBSTRACT = "-";
    public static final String MULTIPLY = "*";
    public static final String DIVIDE = "/";
    public static final String SQUARE_ROOT = "\u221a";
    public static final String SQUARE = "\u00b2";
    public static final String LOG = "log" + '\u2081' + '\u2080';

    public static final Boolean CHECKBOX = true;
    public static final Boolean RADIOBUTTON = false;

    public static final int BASIC = 0; // used for identification if the basic screen level 1 to 5

    public static final int idOfLevel6Question[] =
            {R.id.level6_question0_checkbox,
                    R.id.level6_question1_checkbox,
                    R.id.level6_question2_checkbox,
                    R.id.level6_question3_checkbox};

    public static final int idOfLevel7Question[] =
            {R.id.level7_question0_radiobutton,
                    R.id.level7_question1_radiobutton,
                    R.id.level7_question2_radiobutton,
                    R.id.level7_question3_radiobutton};

    // global variables
    int indexOfRightAnswerAtLevel7 = DEFAULT_DIGIT_VALUE; // used in level 7 to point to the right answer
    int result[] =
            {DEFAULT_DIGIT_VALUE,
                    DEFAULT_DIGIT_VALUE,
                    DEFAULT_DIGIT_VALUE,
                    DEFAULT_DIGIT_VALUE};

    boolean resultOk[] =
            {DEFAULT_BOOLEAN_VALUE,
                    DEFAULT_BOOLEAN_VALUE,
                    DEFAULT_BOOLEAN_VALUE,
                    DEFAULT_BOOLEAN_VALUE};    // Remember if the result of the [i] question is ok or not //

    int scoreOk = DEFAULT_DIGIT_VALUE;
    int scoreNotOk = DEFAULT_DIGIT_VALUE;

    boolean animationOn = false;
    boolean animationSubmitAnswerOn = false;
    int level = DEFAULT_DIGIT_VALUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buildScreen(BASIC); // setup de layout for the basic levels
        setAnimation(); // show were the buttons are
        nextLevel();
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putInt("indexOfRightAnswerAtLevel7", indexOfRightAnswerAtLevel7);
        savedInstanceState.putInt("scoreOk", scoreOk);
        savedInstanceState.putInt("scoreNotOk", scoreNotOk);
        savedInstanceState.putInt("level", level);

        // save level 1 to 5 calculation text
        TextView level1_5_calculation = (TextView) findViewById(R.id.calculationTextView);
        savedInstanceState.putString("calculationString", (String) level1_5_calculation.getText());

        // save the questions of level 6 and level 7
        for (int i = 0; i < 4; i++) {
            savedInstanceState.putInt("result" + i, result[i]);
            CheckBox checkBox = (CheckBox) findViewById(idOfLevel6Question[i]);
            savedInstanceState.putBoolean("checkBoxChecked(" + i + ")", checkBox.isChecked()); // save the checkstatus
            savedInstanceState.putString("checkBox(" + i + ")", (String) checkBox.getText());
            RadioButton radioButton = (RadioButton) findViewById(idOfLevel7Question[i]);
            savedInstanceState.putString("radioButton(" + i + ")", (String) radioButton.getText());
            savedInstanceState.putBoolean("resultOk[" + i + "]", resultOk[i]);
        }

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        savedInstanceState.putInt("radioButtonChecked", radioGroup.getCheckedRadioButtonId());
        savedInstanceState.putBoolean("animationOn", animationOn);
        savedInstanceState.putBoolean("animationSubmitAnswerOn", animationSubmitAnswerOn);

        //LEVEL 8 SAVINGS
        TextView text = (TextView) findViewById(R.id.EditTextview_question);
        savedInstanceState.putString("editTextView", (String) text.getText());
        savedInstanceState.putInt("imeOptions", text.getImeOptions());
    }


    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        indexOfRightAnswerAtLevel7 = savedInstanceState.getInt("indexOfRightAnswerAtLevel7");

        scoreOk = savedInstanceState.getInt("scoreOk");
        scoreNotOk = savedInstanceState.getInt("scoreNotOk");
        level = savedInstanceState.getInt("level");

        // get the questions of level 6 and 7 back
        SharedPreferences settings = getSharedPreferences("answers", MODE_PRIVATE);
        for (int i = 0; i < 4; i++) {
            result[i] = savedInstanceState.getInt("result" + i);
            CheckBox checkBox = (CheckBox) findViewById(idOfLevel6Question[i]);
            checkBox.setText(savedInstanceState.getString("checkBox(" + i + ")"));
            checkBox.setChecked(savedInstanceState.getBoolean("checkBoxChecked(" + i + ")"));
            RadioButton radioButton = (RadioButton) findViewById(idOfLevel7Question[i]);
            radioButton.setText(savedInstanceState.getString("radioButton(" + i + ")"));
            resultOk[i] = savedInstanceState.getBoolean("resultOk[" + i + "]");
        }
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.check(savedInstanceState.getInt("radioButtonChecked"));
        animationOn = savedInstanceState.getBoolean("animationOn");
        animationSubmitAnswerOn = savedInstanceState.getBoolean("animationSubmitAnswerOn");


        //level 8
        TextView text = (TextView) findViewById(R.id.EditTextview_question);
        text.setText(savedInstanceState.getString("editTextView"));
        text.setImeOptions(savedInstanceState.getInt("imeOptions"));

        // restore the state of the animations
        final Animation animation = new AlphaAnimation(VISIBLE, INVISIBLE); // Change alpha from fully visible to invisible
        animation.setDuration(DURATION_BLINKING_BUTTONS); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in

        final Button notOkButton = (Button) findViewById(R.id.notOkButton);
        final Button okButton = (Button) findViewById(R.id.okButton);
        final Button submitAnswerButton = (Button) findViewById(R.id.submitAnwserButton);

        //restore level 1-5
        if (level < 6) {
            hideLayout(R.id.linearlayout_with_checkboxes);
            hideButton(R.id.submitAnwserButton);

            showButton(R.id.notOkButton);
            showButton(R.id.okButton);

            showLayout(R.id.linearlayout_with_digits);

            // restore the state of the animations
            if (animationOn) {
                notOkButton.startAnimation(animation);
                okButton.startAnimation(animation);
            } else {
                okButton.clearAnimation();
                notOkButton.clearAnimation();
            }

            TextView level1_5_calculation = (TextView) findViewById(R.id.calculationTextView);
            level1_5_calculation.setText(savedInstanceState.getString("calculationString"));
        } else {
            switch (level) {
                case 6: // restore checkboxes level 6
                    hideLayout(R.id.linearlayout_with_digits);
                    hideLayout(R.id.linearlayout_with_radiobuttons);
                    hideButton(R.id.notOkButton);
                    hideButton(R.id.okButton);
                    showLayout(R.id.linearlayout_with_checkboxes);
                    showButton(R.id.submitAnwserButton);
                    break;
                case 7: // restore radiogroup level 7
                    hideButton(R.id.notOkButton);
                    hideButton(R.id.okButton);
                    hideLayout(R.id.linearlayout_with_digits);
                    hideLayout(R.id.linearlayout_with_checkboxes);
                    showLayout(R.id.linearlayout_with_radiobuttons);
                    showButton(R.id.submitAnwserButton);
                    break;
                case 8: // restore editView
                    hideButton(R.id.notOkButton);
                    hideButton(R.id.okButton);
                    hideLayout(R.id.linearlayout_with_digits);
                    hideLayout(R.id.linearlayout_with_radiobuttons);
                    showButton(R.id.submitAnwserButton);
                    showLayout(R.id.linearlayout_with_editview);
                    break;
                case 9:
                    hideButton(R.id.notOkButton);
                    hideButton(R.id.okButton);
                    hideLayout(R.id.linearlayout_with_digits);
                    hideLayout(R.id.linearlayout_with_editview);
                    hideButton(R.id.submitAnwserButton);
                    break;
            }
            if (animationSubmitAnswerOn) {
                submitAnswerButton.startAnimation(animation);
            } else {
                submitAnswerButton.clearAnimation();
            }
        }
        // restore scoreboard
        displayInt(R.id.scoreOk_text_view, scoreOk);
        displayInt(R.id.scoreNotOk_text_view, scoreNotOk);
        // restore level picture
        showScoreLevel();
    }


    /**
     * This method is called when the reset button is clicked. It restarts the app.
     */
    public void reset(View view) {
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    /**
     * This method is called when the OK button is clicked. It checks if OK was the right answer to the question and handles the consequences (level Up or Down).
     * It is used from level one to five. All this answers on these levels are put in result[2] and resultOk[2]
     */
    public void ok(View view) {
        if (resultOk[2]) {
            handleOk();
        } else {
            handleNotOk();
        }
        nextLevel();
    }

    /**
     * This method is called when the Not OK button is clicked. It checks if Not OK was the right answer to the question and handles the consequence (level Up or Down).
     * It is used from level one to five
     */
    public void notOk(View view) {
        if (resultOk[2]) {
            handleNotOk();
        } else {
            handleOk();
        }
        nextLevel();
    }

    /**
     * This method is called when the submitAnswer button is clicked at level 6,7 and 8 It checks if the given answer was the right answer to the question and handles the consequence (level Up or Down).
     * It is used from level six to eight.
     */
    public void submitAnswer(View view) {
        // Figure out if the user gave the right answer
        boolean wrongAnswer = false;
        switch (level) {
            case 6:  //checkboxes
                for (int i = 0; i < 4; i++) {
                    if (checkAnswer(idOfLevel6Question[i], i, CHECKBOX) == true) {
                        wrongAnswer = true;
                    }
                }
                if (wrongAnswer == false) {
                    handleOk();
                } else {
                    handleNotOk();
                }
                break;
            case 7:  //radiobuttons
                wrongAnswer = checkAnswer(idOfLevel7Question[indexOfRightAnswerAtLevel7], indexOfRightAnswerAtLevel7, RADIOBUTTON);
                if (wrongAnswer == false) {
                    handleOk();
                } else {
                    handleNotOk();
                }
                break;
            case 8: // textEntry
                int myNum = 0;
                EditText nameField = (EditText) findViewById(editTextAnswer);
                try {
                    myNum = Integer.parseInt(nameField.getText().toString());
                } catch (NumberFormatException nfe) {
                    System.out.println("Could not parse " + nfe);
                }
                if (result[2] == myNum) {
                    handleOk();
                } else {
                    handleNotOk();
                }
                // code to get the keyboard dissapear
                InputMethodManager inputManager =
                        (InputMethodManager) this.
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(
                        this.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                break;
        }
        nextLevel();
    }


    /**
     * Returns a random number between max(parameter) and 1
     *
     * @param max is the maximum value of the chosen number
     * @return random number
     */
    private int generateNumber(int max) {
        int number = 0;
        Random r = new Random();
        number = r.nextInt(max) + 1;
        return number;
    }


    /**
     * Depending on de global difficult level a global operator is generated and a symbol of is is returned as a string
     *
     * @return symbol of the difficult level associated operator
     */
    private String generateOperator(int difficultyLevel) {
        String operator = ADD;

        switch (difficultyLevel) {
            case 1:
                operator = ADD;
                break;
            case 2:
                if (generateNumber(10) <= 5) { // Give it a 50/50 change
                    operator = ADD;
                } else {
                    operator = SUBSTRACT;
                }
                break;
            case 3:
                if (generateNumber(10) <= 5) { // Give it a 50/50 change
                    operator = SUBSTRACT;
                } else {
                    operator = MULTIPLY;
                }
                break;
            case 4:
                if (generateNumber(10) <= 5) { // Give it a 50/50 change
                    operator = MULTIPLY;
                } else {
                    operator = DIVIDE;
                }
                break;
            case 5:
                operator = DIVIDE;
                break;
            case 6:
                if (generateNumber(10) <= 5) { // Give it a 50/50 change
                    operator = SQUARE_ROOT;
                } else {
                    operator = SQUARE;
                }
                break;
            case 7:
                if (generateNumber(10) <= 5) { // Give it a 50/50 change
                    operator = SQUARE_ROOT;
                } else {
                    operator = LOG;
                }
                break;
        }
        return operator;
    }


    /**
     * check on level 6 and 7 if one of the four answers  equals with one of the others. If so then return true other wise return a false
     *
     * @param index points to the index of the result/resultOK array. Look if one of the others is the same.
     * @return result of the check true= one of the result is equal, fals = no match
     */
    private boolean equalToOneOfTheOtherAnswers(int index) {
        boolean answer = false;
        for (int i = 0; i < index; i++) {
            // only one correct answer should be possible (requirement)
            if (result[index] == result[i]) {
                answer = true;
            }
        }
        return answer;
    }


    /**
     * Calculate a result of a calculatie that can be  the price of the order.
     *
     * @param truth indicator of the result should be the right answer, a false answer
     * @param var1  variable one for the calculation
     * @param var2  second variable one for the calculation
     * @return result of the calculation
     */
    private int generateResult(boolean truth, int var1, int var2, String doIt) {
        int answer = 0;
        if (truth == true) {
            answer = calculate(var1, var2, doIt);
        } else {
            do {
                answer = generateNumber(10);
            } while (answer == calculate(var1, var2, doIt));
        }
        return answer;
    }


    /**
     * calculate the result of var1 and var2 operatated with different operators
     *
     * @param var1 the first variable
     * @param var2 the second variable
     * @return the result of the global firstDigit, operator and secondDigit
     */
    private int calculate(int var1, int var2, String doIt) {
        int x = 0;
        switch (doIt) {
            case ADD:
                x = var1 + var2;
                break;
            case SUBSTRACT:
                x = var1 - var2;
                break;
            case DIVIDE:
                x = var1 / var2;
                break;
            case MULTIPLY:
                x = var1 * var2;
                break;
            case SQUARE_ROOT:
                x = (int) Math.sqrt(var1);
                break;
            case SQUARE:
                x = var1 * var1;
                break;
            case LOG:
                x = (int) Math.log10(var1);
                break;
        }
        return x;
    }


    /**
     * Displays an integer digit in the level 1 to 5 screen.
     *
     * @param id    is de ID of the textView  where the digit should be displayed
     * @param digit is the number that must be displayed
     */
    private void displayInt(int id, int digit) {
        TextView scoreLevelTextView = (TextView) findViewById(id);
        scoreLevelTextView.setText(String.valueOf(digit));
    }

    /**
     * Displays an String in the level 1 to 5 screen.
     *
     * @param id    is de ID of the textView  where the String should be displayed
     * @param digit is the String that must be displayed
     */
    private void displayString(int id, String digit) {
        TextView scoreLevelTextView = (TextView) findViewById(id);
        scoreLevelTextView.setText(digit);
    }

    /**
     * Remove a linearlayout from the screen.
     *
     * @param id is de ID of the linearlayout
     */
    private void hideLayout(int id) {
        LinearLayout linearLayout = (LinearLayout) this.findViewById(id);
        linearLayout.setVisibility(View.GONE);
    }

    /**
     * Remove a button from the screen that is defined in the activity_main.xml.
     *
     * @param id is de ID of the button that will be removed
     */
    private void hideButton(int id) {
        Button button = (Button) this.findViewById(id);
        button.setVisibility(View.GONE);
    }

    /**
     * Makes a button that is defined in the activity_main.xml appear on the screen.
     *
     * @param id is de ID of the button that should appear
     */
    private void showButton(int id) {
        Button button = (Button) findViewById(id);
        button.setVisibility(View.VISIBLE);
    }

    /**
     * Makes a LinearLayout that is defined in the activity_main.xml appear on the screen.
     *
     * @param id is de ID of the  LinearLayout that should appear
     */
    private void showLayout(int id) {
        LinearLayout linearLayout = (LinearLayout) this.findViewById(id);
        linearLayout.setVisibility(View.VISIBLE);
    }


    /**
     * Makes a Toast Message appear on the screen.
     *
     * @param encourage is de text that should appear on the screen
     */
    private void displayToast(String encourage) {
        View view;
        TextView text;
        Toast toast = Toast.makeText(this, encourage, Toast.LENGTH_SHORT);
        view = toast.getView();
        text = (TextView) view.findViewById(message);
        text.setTextColor(getResources().getColor(holo_red_dark));
        view.setBackgroundResource(R.drawable.resetbutton);
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    /**
     * After an answer is determinded to be the right answer the level(global) is incremented and the score is updated on the screen.
     * also an encouraging message is displayed with a Toast
     */
    private void handleOk() {
        scoreOk += 1;
        level = Math.min(level + 1, MAXIMUM_CALC_LEVEL);
        displayInt(R.id.scoreOk_text_view, scoreOk);
        displayToast(getString(R.string.okMessage));
    }

    /**
     * After an answer is determinded to be the wrong answer the level(global) is decremented and the Not Ok score is updated on the screen.
     * also a comforting message :-) is displayed with a Toast
     */
    private void handleNotOk() {
        scoreNotOk += 1;
        level = Math.max(level - 1, 1); // not lower then level 1
        displayInt(R.id.scoreNotOk_text_view, scoreNotOk);
        displayToast(getString(R.string.notOkMessage));
    }

    /**
     * After the user clicks the "submit answer" button this method is called to check if the answer is ok or not
     * only used in level 6 and 7 with the checkboxes and the radiogroup. If the answer is not OK a global flag: wronganswer is set on true
     *
     * @param boxId    is de ID of the checkbox or radiobutton to be checked
     * @param answerId is de ID of the indicator in resultOK if the result was true or false
     * @param type     indicator of the boxId refers to a checkbox or a radiobutton
     */
    private boolean checkAnswer(int boxId, int answerId, boolean type) {
        boolean wrongAnswer = false;
        if (type == CHECKBOX) {
            CheckBox box = (CheckBox) findViewById(boxId);
            if (box.isChecked() != resultOk[answerId]) {
                displayInt(R.id.scoreNotOk_text_view, scoreNotOk);
                wrongAnswer = true;
            } else {
                displayInt(R.id.scoreOk_text_view, scoreOk);
            }
        } else {
            RadioButton box = (RadioButton) findViewById(boxId);
            if (box.isChecked() != resultOk[answerId]) {
                displayInt(R.id.scoreNotOk_text_view, scoreNotOk);
                wrongAnswer = true;
            } else {
                displayInt(R.id.scoreOk_text_view, scoreOk);
            }
        }
        return wrongAnswer;
    }

    /**
     * Uncheck a checkbox
     *
     * @param boxId is de ID of the checkbox to be unchecked
     */
    private void clearCheckBoxAnswer(int boxId) {
        CheckBox box = (CheckBox) findViewById(boxId);
        box.setChecked(false);
    }

    private void displayBasicCalculation(int var1, String operator, int var2, int answer) {
        String text = String.valueOf(var1) + operator + String.valueOf(var2) + "=" + String.valueOf(answer);
        displayString(R.id.calculationTextView, text);
    }

    /**
     * generate depending of parameter chance: true or false
     *
     * @param chance is the 100/chance change on a true value 100 = always 0=never
     */
    private boolean generateTruth(int chance) {
        boolean temp;
        // Give it a chance of chance/100) {
        temp = generateNumber(100) <= chance;
        return temp;
    }


    /**
     * After each answer the level goes up or down depending if the answer was right or wrong
     * each level has its dedicated global variables
     */
    private void nextLevel() {
        // reset all globals except for the level,score indicators and animation
        indexOfRightAnswerAtLevel7 = DEFAULT_DIGIT_VALUE; // used in level 7 to point to the right answer

        boolean truth = false;
        String calString = "";
        int num6 = 0;
        int num7 = 0;
        int num8 = 0;

        for (int i = 0; i < 4; i++) {
            resultOk[i] = false;
            result[i] = DEFAULT_DIGIT_VALUE;
            clearCheckBoxAnswer(idOfLevel6Question[i]);
        }
        RadioGroup button = (RadioGroup) findViewById(R.id.radioGroup);
        button.clearCheck();

        EditText answer = (EditText) findViewById(R.id.editTextAnswer);
        answer.setText("");

        // from her the level build up
        if (level < 6) {
            buildScreen(BASIC);
            result[0] = generateNumber(9);
            result[1] = generateNumber(9);
            calString = generateOperator(level);

            if (calString.compareTo(DIVIDE) == 0) {
                result[0] = result[0] * result[1]; // to get an nice integer as result of the divide
            }
            truth = generateTruth(50);        // generate randomly a true or false answer 50% chance
            result[2] = generateResult(truth, result[0], result[1], calString);
            resultOk[2] = truth;
            displayBasicCalculation(result[0], calString, result[1], result[2]);
            displayInt(R.id.scoreNotOk_text_view, scoreNotOk);
            displayInt(R.id.scoreOk_text_view, scoreOk);
        } else {
            switch (level) {
                case 6: // checkboxes
                    buildScreen(6);
                    for (int i = 0; i < 4; i++) {
                        do {
                            calString = generateOperator(6); // generates an operator for level 6
                            num6 = generateNumber(9); //
                            if (calString.compareTo(SQUARE_ROOT) == 0) {
                                num6 = num6 * num6; // to get an nice integer as result of the SQUARE ROOT
                            }
                            truth = generateTruth(70); // 70 percent chance that the answer is right

                            result[i] = generateResult(truth, num6, 0, calString); // Generate a result and remember if it is a right answer in result[i]
                            resultOk[i] = truth;
                        }
                        while (equalToOneOfTheOtherAnswers(i) == true); // it should be all different answers
                        showCheckBoxQuestion(idOfLevel6Question[i], calString, num6, result[i]);
                    }
                    break;
                case 7: //radiogroup: only one answer is the right answer
                    buildScreen(7);
                    indexOfRightAnswerAtLevel7 = generateNumber(3); // the indez of the right answer 0..3
                    calString = LOG;
                    num7 = (int) Math.pow(10, generateNumber(4)); // generate a number for a nice result with maximum of 10000)
                    result[indexOfRightAnswerAtLevel7] = generateResult(true, num7, 0, calString);
                    resultOk[indexOfRightAnswerAtLevel7] = true;
                    showRadioButtonQuestion(idOfLevel7Question[indexOfRightAnswerAtLevel7], calString, num7, result[indexOfRightAnswerAtLevel7]);
                    for (int i = 0; i < 4; i++) {
                        if (i != indexOfRightAnswerAtLevel7) {// only one correct answer should be possible (requirement)
                            do {
                                result[i] = generateResult(false, num7, 0, calString); // wrong answer
                            } while (equalToOneOfTheOtherAnswers(i) == true);
                            showRadioButtonQuestion(idOfLevel7Question[i], calString, num7, result[i]);
                        }
                    }
                    break;
                case 8: // open question so resultOk is not used
                    buildScreen(8);
                    calString = LOG;
                    num8 = (int) Math.pow(10, generateNumber(4));
                    result[0] = generateResult(true, num8, 0, calString); // Generate a result and remember if it is a right answer in result[i]
                    String text = calString + '(' + String.valueOf(num8) + ")";
                    calString = SQUARE_ROOT;
                    num8 = generateNumber(MAXIMUM_DIGIT_VALUES);
                    num8 = num8 * num8; // to get a nice integer as square root
                    result[1] = generateResult(true, num8, 0, calString); // Generate a result and remember if it is a right answer in result[i]
                    text = text + "+" + calString + String.valueOf(num8) + "= ?";
                    result[2] = result[0] + result[1];
                    displayString(R.id.EditTextview_question, text);

                    // set focus on keyboard
                    answer.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(answer, InputMethodManager.SHOW_IMPLICIT);
                    break;
                case 9:
                    buildScreen(9);
                    displayToast(getString(R.string.finalMessage));
                    break;
            }
        }

    }

    /**
     * a the difficulty level a scorelevel is introduced. It is defined as the difference of the totals of right and wrong answers
     * Level 0 till 5 are supported with a smiling sun that grows with each level
     * scorelevel under zero displays a rainy cloud
     */
    private void showScoreLevel() {
        int scoreLevel = scoreOk - scoreNotOk;
        ImageView scoreLevelImageView = (ImageView) findViewById(R.id.scoreLevel);

        if (scoreLevel < 0) {
            scoreLevelImageView.getLayoutParams().width = (int) getResources().getDimension(R.dimen.scoreLevel5_imageview_width);
            scoreLevelImageView.setImageResource(cloud);
            scoreLevelImageView.requestLayout();
        } else {
            switch (scoreLevel) {
                case 1:
                    scoreLevelImageView.getLayoutParams().width = (int) getResources().getDimension(R.dimen.scoreLevel_imageview_width);
                    scoreLevelImageView.setImageResource(sun);
                    scoreLevelImageView.requestLayout();
                    break;
                case 2:
                    scoreLevelImageView.getLayoutParams().width = (int) getResources().getDimension(R.dimen.scoreLevel2_imageview_width);
                    scoreLevelImageView.requestLayout();
                    break;
                case 3:
                    scoreLevelImageView.getLayoutParams().width = (int) getResources().getDimension(R.dimen.scoreLevel3_imageview_width);
                    scoreLevelImageView.requestLayout();
                    break;
                case 4:
                    scoreLevelImageView.getLayoutParams().width = (int) getResources().getDimension(R.dimen.scoreLevel4_imageview_width);
                    scoreLevelImageView.requestLayout();
                    break;
                case 5:
                    scoreLevelImageView.getLayoutParams().width = (int) getResources().getDimension(R.dimen.scoreLevel5_imageview_width);
                    scoreLevelImageView.requestLayout();
                    break;
            }
        }
    }


    /**
     * displays a variable math question after each checkbox. Each result displayed can be right of wrong
     * only square and square root calculation questions
     *
     * @param id        is de ID of the checkbox defined in the activity_main.xml
     * @param calString is de symbol for the operator of the calculation
     * @param digit     is de value of the variable to calculate
     * @param result    is de number that must be displayed and can be right or wrong
     */
    private void showCheckBoxQuestion(int id, String calString, int digit, int result) {
        TextView textView = (TextView) findViewById(id);
        String text = "";
        if (calString.compareTo(SQUARE) == 0) {   // the layout of a SQUARE operator
            text = String.valueOf(digit) + calString + " = " + String.valueOf(result);
        } else {                    // the layout of a LOGARTME operator
            text = calString + String.valueOf(digit) + " = " + String.valueOf(result);
        }
        textView.setText(String.valueOf(text));
    }


    /**
     * displays a "logaritm of 10" math question for each radiobutton. Only one result can be right
     *
     * @param id       is de ID of the radiobutton defined in the activity_main.xml
     * @param operator is de symbol for the logaritm
     * @param digit    is de value of the variable to calculate
     * @param result   is de number that must be displayed and can be right or wrong
     */
    private void showRadioButtonQuestion(int id, String operator, int digit, int result) {
        TextView textView = (TextView) findViewById(id);
        String text = "";
        text = operator + '(' + String.valueOf(digit) + ") = " + String.valueOf(result);
        textView.setText(String.valueOf(text));
    }


    /**
     * handels the screenlayout for the different difficulty levels
     *
     * @param screenType is de ID of the diffulty level and the associated screen
     */
    private void buildScreen(int screenType) {
        switch (screenType) {
            case BASIC:
                hideLayout(R.id.linearlayout_with_editview);
                hideLayout(R.id.linearlayout_with_checkboxes);
                hideLayout(R.id.linearlayout_with_radiobuttons);
                hideButton(R.id.submitAnwserButton);
                showButton(R.id.notOkButton);
                showButton(R.id.okButton);
                showLayout(R.id.linearlayout_with_digits);
                showScoreLevel();
                break;
            case 6:
                hideLayout(R.id.linearlayout_with_digits);
                hideLayout(R.id.linearlayout_with_radiobuttons);
                hideButton(R.id.notOkButton);
                hideButton(R.id.okButton);
                showLayout(R.id.linearlayout_with_checkboxes);
                showButton(R.id.submitAnwserButton);
                showScoreLevel();
                break;
            case 7:
                hideLayout(R.id.linearlayout_with_editview);
                hideLayout(R.id.linearlayout_with_checkboxes);
                showLayout(R.id.linearlayout_with_radiobuttons);
                showScoreLevel();
                break;
            case 8:
                hideLayout(R.id.linearlayout_with_radiobuttons);
                showLayout(R.id.linearlayout_with_editview);
                showScoreLevel();
                break;
            case 9:
                hideLayout(R.id.linearlayout_with_editview);
                hideButton(R.id.submitAnwserButton);
                showScoreLevel();
                break;
        }
    }


    /**
     * with the first display of button the button are blinkingto show where the are.
     * After the first click the blinking disappears
     */
    private void setAnimation() {
        final Button notOkButton = (Button) findViewById(R.id.notOkButton);
        final Button okButton = (Button) findViewById(R.id.okButton);
        final Button submitAnswerButton = (Button) findViewById(R.id.submitAnwserButton);
        Animation animation = new AlphaAnimation(VISIBLE, INVISIBLE); // Change alpha from fully visible to invisible
        animation.setDuration(DURATION_BLINKING_BUTTONS); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in

        notOkButton.startAnimation(animation);
        okButton.startAnimation(animation);
        submitAnswerButton.startAnimation(animation);
        animationOn = true;
        animationSubmitAnswerOn = true;

        notOkButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                view.clearAnimation();
                animationOn = false;
                okButton.clearAnimation();
                notOk(view);  // go to the notOk operation
            }
        });

        okButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                view.clearAnimation();
                animationOn = false;
                notOkButton.clearAnimation();
                ok(view);  // go to the ok opereration
            }
        });

        submitAnswerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                animationSubmitAnswerOn = false;
                view.clearAnimation();
                submitAnswer(view);  // go to the submitAnswer opereration
            }
        });
    }
}
