package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.mariuszgromada.math.mxparser.Expression;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText display;
    private String entryStr = "";
    private boolean isResult = false;
    //ArrayList<String> entryStack = new ArrayList<String>();

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
    }

    /*private void updateText(String strToAdd){
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
    }*/

    private String getInputText(){
        EditText itxt = (EditText)findViewById(R.id.input);
        return itxt.getText().toString();
    }

    //Clears entry if the entry is showing result
    public void showingResult(){
        if (isResult) {
            entryStr += "";
            display.setText(entryStr);
            isResult = false;
        }
    }

    public void alphaNumUpdate(){
        if (!entryStr.equals("") && isResult) {
            entryStr += "";
            display.setText(entryStr);
            isResult = false;
        } else if (!entryStr.equals("") && entryStr.matches(".*[-+/*√)^%]")) {
            entryStr += " ";
            display.setText(entryStr);
        }
    }
    public void operatorUpdate(){
        if (!entryStr.equals("") && !entryStr.matches(".*[ ]")) {
            entryStr += " ";
            display.setText(entryStr);
        }
        if (isResult) {
            isResult = false;
        }
    }

    public void zeroBTN(View view){
        alphaNumUpdate();
        entryStr += "0";
        display.setText(entryStr);
    }
    public void oneBTN(View view){
        alphaNumUpdate();
        entryStr += "1";
        display.setText(entryStr);
    }
    public void twoBTN(View view){
        alphaNumUpdate();
        entryStr += "2";
        display.setText(entryStr);
    }
    public void threeBTN(View view){
        alphaNumUpdate();
        entryStr += "3";
        display.setText(entryStr);
    }
    public void fourBTN(View view){
        alphaNumUpdate();
        entryStr += "4";
        display.setText(entryStr);
    }
    public void fiveBTN(View view){
        alphaNumUpdate();
        entryStr += "5";
        display.setText(entryStr);
    }
    public void sixBTN(View view){
        alphaNumUpdate();
        entryStr += "6";
        display.setText(entryStr);
    }
    public void sevenBTN(View view){
        alphaNumUpdate();
        entryStr += "7";
        display.setText(entryStr);
    }
    public void eightBTN(View view){
        alphaNumUpdate();
        entryStr += "8";
        display.setText(entryStr);
    }
    public void nineBTN(View view){
        alphaNumUpdate();
        entryStr += "9";
        display.setText(entryStr);
    }
    public void aBTN(View view){
        alphaNumUpdate();
        entryStr += "A";
        display.setText(entryStr);
    }
    public void bBTN(View view){
        alphaNumUpdate();
        entryStr += "B";
        display.setText(entryStr);
    }
    public void cBTN(View view){
        alphaNumUpdate();
        entryStr += "C";
        display.setText(entryStr);
    }
    public void dBTN(View view){
        alphaNumUpdate();
        entryStr += "D";
        display.setText(entryStr);
    }
    public void eBTN(View view){
        alphaNumUpdate();
        entryStr += "E";
        display.setText(entryStr);
    }
    public void fBTN(View view){
        alphaNumUpdate();
        entryStr += "F";
        display.setText(entryStr);
    }
    public void decimalBTN(View view){
        alphaNumUpdate();
        entryStr += ".";
        display.setText(entryStr);
    }
    public void piBTN(View view){
        //alphaNumUpdate();
        entryStr += "π";
        display.setText(entryStr);
    }

    public void expBTN(View view){
        operatorUpdate();
        entryStr += "^";
        display.setText(entryStr);
    }
    public void squareRootBTN(View view){
        operatorUpdate();
        entryStr += "√";
        display.setText(entryStr);
    }

    public void multiplyBTN(View view){
        operatorUpdate();
        entryStr += "*";
        display.setText(entryStr);
    }
    public void divideBTN(View view){
        operatorUpdate();
        entryStr += "/";
        display.setText(entryStr);
    }
    public void addBTN(View view){
        operatorUpdate();
        entryStr += "+";
        display.setText(entryStr);
    }
    public void subtractBTN(View view){
        operatorUpdate();
        entryStr += "-";
        display.setText(entryStr);
    }
    public void moduloBTN(View view){
        operatorUpdate();
        entryStr += "%";
        display.setText(entryStr);
    }

    public void parBTN(View view){
        showingResult();
        int cursorPos = display.getSelectionStart();
        int openPar = StringUtils.countMatches(entryStr, "(");
        int closedPar = StringUtils.countMatches(entryStr, ")");

        if ((openPar == closedPar) || entryStr.matches(".*[(]")){
            alphaNumUpdate();
            entryStr += "(";
            display.setText(entryStr);
        }
        else if ((closedPar < openPar) && !entryStr.matches(".*[(]")){
            entryStr += ")";
            display.setText(entryStr);
        }
        display.setSelection(cursorPos+1);
    }

    public void clearBTN(View view){
        if (!getInputText().equals("")) {
            entryStr = "";
            display.setText(entryStr);
        }
    }

    public void spaceBTN(View view){
        if (!entryStr.matches(".*[ ]")) {
            entryStr += " ";
            display.setText(entryStr);
        }
    }

    public void equalBTN(View view){
        operatorUpdate();
        Expression e = new Expression(entryStr);
        display.setText("");
        double r = e.calculate();
        if ((r % 1) == 0) {
            entryStr = String.valueOf((int) r);
            display.setText(entryStr);
        } else {
            entryStr = String.valueOf(r);
            display.setText(entryStr);
        }
        isResult = true;
    }

    public void backspaceBTN(View view){
        showingResult();

        if (!entryStr.equals("")){
            entryStr = entryStr.substring(0, entryStr.length() - 1);
            display.setText(entryStr);
        }
    }


}