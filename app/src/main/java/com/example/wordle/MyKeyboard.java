package com.example.wordle;

import static android.view.inputmethod.EditorInfo.IME_ACTION_NEXT;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.HashMap;

/**
 * This is the custom class for the on-screen custom keyboard used for the Wordle Application
 * @author Hong Wei Chen
 */
public class MyKeyboard extends LinearLayout implements View.OnClickListener {
    private Button buttonQ, buttonW, buttonE, buttonR, buttonT, buttonY, buttonU, buttonI, buttonO, buttonP,
                    buttonA, buttonS, buttonD, buttonF, buttonG, buttonH, buttonJ, buttonK, buttonL,
                    buttonEnter, buttonZ, buttonX, buttonC, buttonV, buttonB, buttonN, buttonM, buttonDelete;

    private HashMap<Integer, String> keyValues = new HashMap<Integer, String>();
    private InputConnection inputConnection;

    public MyKeyboard(Context context){
        this(context, null, 0);
        init(context,null);
    }

    public MyKeyboard(Context context, AttributeSet attr){
        this(context, attr, 0);
        init(context, attr);
    }

    public MyKeyboard(Context context, AttributeSet attr, int defStyle){
        super(context, attr, defStyle);
        init(context, attr);
    }

    private void init(Context context, AttributeSet attr){
        //inflate the view
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.keyboard, this, true);

        //assign onclicklisteners
        buttonQ = findViewById(R.id.button_q);
        buttonQ.setOnClickListener(this);
        buttonW = findViewById(R.id.button_w);
        buttonW.setOnClickListener(this);
        buttonE = findViewById(R.id.button_e);
        buttonE.setOnClickListener(this);
        buttonR = findViewById(R.id.button_r);
        buttonR.setOnClickListener(this);
        buttonT = findViewById(R.id.button_t);
        buttonT.setOnClickListener(this);
        buttonY = findViewById(R.id.button_y);
        buttonY.setOnClickListener(this);
        buttonU = findViewById(R.id.button_u);
        buttonU.setOnClickListener(this);
        buttonI = findViewById(R.id.button_i);
        buttonI.setOnClickListener(this);
        buttonO = findViewById(R.id.button_o);
        buttonO.setOnClickListener(this);
        buttonP = findViewById(R.id.button_p);
        buttonP.setOnClickListener(this);
        buttonA = findViewById(R.id.button_a);
        buttonA.setOnClickListener(this);
        buttonS = findViewById(R.id.button_s);
        buttonS.setOnClickListener(this);
        buttonD = findViewById(R.id.button_d);
        buttonD.setOnClickListener(this);
        buttonF = findViewById(R.id.button_f);
        buttonF.setOnClickListener(this);
        buttonG = findViewById(R.id.button_g);
        buttonG.setOnClickListener(this);
        buttonH = findViewById(R.id.button_h);
        buttonH.setOnClickListener(this);
        buttonJ = findViewById(R.id.button_j);
        buttonJ.setOnClickListener(this);
        buttonK = findViewById(R.id.button_k);
        buttonK.setOnClickListener(this);
        buttonL = findViewById(R.id.button_l);
        buttonL.setOnClickListener(this);
        buttonEnter = findViewById(R.id.button_enter);
        buttonEnter.setOnClickListener(this);
        buttonZ = findViewById(R.id.button_z);
        buttonZ.setOnClickListener(this);
        buttonX = findViewById(R.id.button_x);
        buttonX.setOnClickListener(this);
        buttonC = findViewById(R.id.button_c);
        buttonC.setOnClickListener(this);
        buttonV = findViewById(R.id.button_v);
        buttonV.setOnClickListener(this);
        buttonB = findViewById(R.id.button_b);
        buttonB.setOnClickListener(this);
        buttonN = findViewById(R.id.button_n);
        buttonN.setOnClickListener(this);
        buttonM = findViewById(R.id.button_m);
        buttonM.setOnClickListener(this);
        buttonDelete = findViewById(R.id.button_delete);
        buttonDelete.setOnClickListener(this);


        //populate the key-value bindings
        keyValues.put(R.id.button_q, "Q");
        keyValues.put(R.id.button_w, "W");
        keyValues.put(R.id.button_e, "E");
        keyValues.put(R.id.button_r, "R");
        keyValues.put(R.id.button_t, "T");
        keyValues.put(R.id.button_y, "Y");
        keyValues.put(R.id.button_u, "U");
        keyValues.put(R.id.button_i, "I");
        keyValues.put(R.id.button_o, "O");
        keyValues.put(R.id.button_p, "P");
        keyValues.put(R.id.button_a, "A");
        keyValues.put(R.id.button_s, "S");
        keyValues.put(R.id.button_d, "D");
        keyValues.put(R.id.button_f, "F");
        keyValues.put(R.id.button_g, "G");
        keyValues.put(R.id.button_h, "H");
        keyValues.put(R.id.button_j, "J");
        keyValues.put(R.id.button_k, "K");
        keyValues.put(R.id.button_l, "L");
        keyValues.put(R.id.button_z, "Z");
        keyValues.put(R.id.button_x, "X");
        keyValues.put(R.id.button_c, "C");
        keyValues.put(R.id.button_v, "V");
        keyValues.put(R.id.button_b, "B");
        keyValues.put(R.id.button_n, "N");
        keyValues.put(R.id.button_m, "M");
    }

    @Override
    public void onClick(View view){
        if (inputConnection == null){
            return;
        }

        //Delete key functionality
        if (view.getId() == R.id.button_delete){
            CharSequence selectText = inputConnection.getSelectedText(0);

            if (TextUtils.isEmpty(selectText)){
                inputConnection.deleteSurroundingText(1, 0);
            }
            else{
                inputConnection.commitText("", 1);
            }
        }
        //Enter key functionality
        else if (view.getId() == R.id.button_enter){
            inputConnection.performEditorAction(IME_ACTION_NEXT);
        }
        //Letter key functionality
        else {
            String value = keyValues.get(view.getId());
            inputConnection.commitText(value, 1);
        }
    }

    public HashMap<Integer, String> getKeyValues(){
        return keyValues;
    }

    public void setInputConnection(InputConnection ic){
        inputConnection = ic;
    }
    public void closeInputConnection() {
        if (inputConnection!=null){
            inputConnection=null;
        }
    }
}
