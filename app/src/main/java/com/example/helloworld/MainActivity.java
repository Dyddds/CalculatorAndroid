package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.EditText;

import org.mariuszgromada.math.mxparser.Expression;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText display;
    private EditText resDisplay;
    private String calcStr = "";
    private String entryStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        display = findViewById(R.id.input);
        //display.setShowSoftInputOnFocus(false);
        display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getString(R.string.display).equals(display.getText().toString())){
                    display.setText("");
                }
            }
        });
        resDisplay = findViewById(R.id.result);
        //display.setShowSoftInputOnFocus(false);
        resDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getString(R.string.display).equals(resDisplay.getText().toString())){
                    resDisplay.setText("");
                }
            }
        });
    }
    private void updateText(String strToAdd){
        String oldStr = display.getText().toString();
        int cursorPos = display.getSelectionStart();
        String leftStr = oldStr.substring(0,cursorPos);
        String rightStr = oldStr.substring(cursorPos);
        if(getString(R.string.display).equals(display.getText().toString())){
            display.setText(strToAdd);
        }
        else {
        display.setText(String.format("%s%s%s", leftStr, strToAdd, rightStr));
        }
        display.setSelection(cursorPos+1);
    }

    private void updateTextFromFront(String strToAdd){
        String oldStr = display.getText().toString();
        int cursorPos = display.getSelectionStart();
        String leftStr = oldStr.substring(0,cursorPos);
        String rightStr = oldStr.substring(cursorPos);
        if(getString(R.string.display).equals(display.getText().toString())){
            display.setText(strToAdd);
        }
        else {
            display.setText(String.format("%s%s%s", strToAdd, leftStr, rightStr));
        }
        display.setSelection(cursorPos+1);
    }

    private void removeFrontChar() {
        String oldStr = display.getText().toString();
        if (oldStr.isEmpty()) {
            return;
        }
        int cursorPos = display.getSelectionStart();
        display.setText(oldStr.substring(1));
        display.setSelection(cursorPos - 1);
    }

    //Clears entry if the entry is showing result
    public void showingResult(){
        if (calcStr == "") {
            display.setText("");
        }
    }

    public void zeroBTN(View view){
        showingResult();
        updateText("0");
        calcStr += "0";
    }
    public void oneBTN(View view){
        showingResult();
        updateText("1");
        calcStr += "1";
    }
    public void twoBTN(View view){
        showingResult();
        updateText("2");
        calcStr += "2";
    }
    public void threeBTN(View view){
        showingResult();
        updateText("3");
        calcStr += "3";
    }
    public void fourBTN(View view){
        showingResult();
        updateText("4");
        calcStr += "4";
    }
    public void fiveBTN(View view){
        showingResult();
        updateText("5");
        calcStr += "5";
    }
    public void sixBTN(View view){
        showingResult();
        updateText("6");
        calcStr += "6";
    }
    public void sevenBTN(View view){
        showingResult();
        updateText("7");
        calcStr += "7";
    }
    public void eightBTN(View view){
        showingResult();
        updateText("8");
        calcStr += "8";
    }
    public void nineBTN(View view){
        showingResult();
        updateText("9");
        calcStr += "9";
    }
    public void aBTN(View view){
        showingResult();
        updateText("A");
        //calcStr += "A";
    }
    public void bBTN(View view){
        showingResult();
        updateText("B");
        //calcStr += "B";
    }
    public void cBTN(View view){
        showingResult();
        updateText("C");
        //calcStr += "C";
    }
    public void dBTN(View view){
        showingResult();
        updateText("D");
        //calcStr += "D";
    }
    public void eBTN(View view){
        showingResult();
        updateText("E");
        //calcStr += "E";
    }
    public void fBTN(View view){
        showingResult();
        updateText("F");
        //calcStr += "F";
    }
    public void exponentBTN(View view){
        showingResult();
        updateText("^");
        calcStr += "^";
    }
    public void squarerootBTN(View view){
        showingResult();
        updateText("√");
        calcStr += "sqrt";
    }
    public void piBTN(View view){
        showingResult();
        updateText("π");
        calcStr += "π";

    }

    public void multiplyBTN(View view){
        showingResult();
        updateText("*");
        calcStr += "*";
    }
    public void divideBTN(View view){
        showingResult();
        updateText("/");
        calcStr += "/";
    }
    public void addBTN(View view){
        showingResult();
        updateText("+");
        calcStr += "+";
    }
    public void subtractBTN(View view){
        showingResult();
        updateText("-");
        calcStr += "-";
    }
    public void moduloBTN(View view){
        showingResult();
        updateText("%");
        calcStr += "#";
    }

    public void parBTN(View view){
        showingResult();
        int cursorPos = display.getSelectionStart();
        int openPar = 0;
        int closedPar = 0;
        int textLen = display.getText().length();

        for (int i=0;i< cursorPos;i++){
            if(display.getText().toString().substring(i,i+1).equals("(")){
                openPar += 1;
            }
            if(display.getText().toString().substring(i,i+1).equals(")")){
                closedPar += 1;
            }
        }
        if (openPar == closedPar || display.getText().toString().substring(textLen-1,textLen).equals("(")){
            updateText("(");
            calcStr += "(";
        }
        else if (closedPar < openPar && !display.getText().toString().substring(textLen-1,textLen).equals("(")){
            updateText(")");
            calcStr += ")";
        }
        display.setSelection(cursorPos+1);
    }
    public void clearBTN(View view){
        display.setText("");
        calcStr = "";
    }
    public void expBTN(View view){
        showingResult();
        updateText("^");
        calcStr += "^";
    }
    public void plusMinusBTN(View view){
        showingResult();

        if (calcStr.startsWith("-")) {
            removeFrontChar();
            calcStr = calcStr.substring(1);
        } else {
            updateTextFromFront("-");
            calcStr = "-" + calcStr;
        }

    }
    public void decimalBTN(View view){
        showingResult();
        updateText(".");
        calcStr += ".";
    }
    public void equalBTN(View view){
        showingResult();
        Expression e = new Expression(calcStr);
        display.setText("");
        double r = e.calculate();
        //int rI;
        if ((r % 1) == 0) {
            //rI = (int) rD;
            updateText(String.valueOf((int) r));
        } else {
            updateText(String.valueOf(r));
        }
        calcStr = "";
    }

    public void backspaceBTN(View view){
        showingResult();
        int cursorPos = display.getSelectionStart();
        int textLen = display.getText().length();
        if (cursorPos != 0 && textLen !=0){
            SpannableStringBuilder selection = (SpannableStringBuilder) display.getText();
            selection.replace(cursorPos-1,cursorPos,"");
            calcStr = calcStr.substring(0, calcStr.length() - 1);
            display.setText(selection);
            display.setSelection(cursorPos-1);
        }
    }


}