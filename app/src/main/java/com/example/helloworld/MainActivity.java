package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.mariuszgromada.math.mxparser.Expression;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    enum Notation {
        Prefix, Infix, Postfix
    }
    enum Base {
        Binary, Octal, Decimal, Duodecimal, Hexadecimal
    }

    Notation nMode = Notation.Prefix;
    Base bMode = Base.Decimal;
    private EditText display;
    private String entryStr = "";
    ArrayList<String> history = new ArrayList<String>();
    private boolean isResult = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        display = findViewById(R.id.input);
        //display.setShowSoftInputOnFocus(false);
        //enforceNotationButtons();
        //enforceBaseButtons();
        display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getString(R.string.display).equals(display.getText().toString())){
                    display.setText("");
                }
            }
        });
    }

    private String prefixToInfix(ArrayList<String> preStack){
        ArrayList<String> infixStack = new ArrayList<String>();
        for (int i = preStack.size()-1; i >= 0 ; i--) {
            String str = preStack.get(i);
            if (str.matches(".*[-+/*^%]") && i < preStack.size()-2) {
                String exp = "(" + infixStack.get(infixStack.size()-1) + str + infixStack.get(infixStack.size()-2) + ")";
                infixStack.remove(infixStack.size() - 1);
                infixStack.remove(infixStack.size() - 1);
                infixStack.add(exp);
            } else {
                infixStack.add(str);
            }
        }
        return String.join("", infixStack);
    }
    private String postfixToInfix(ArrayList<String> postStack){
        ArrayList<String> infixStack = new ArrayList<String>();
        for (int i = 0; i < postStack.size() ; i++) {
            String str = postStack.get(i);
            if (str.matches(".*[-+/*^%]") && postStack.size() > 2) {
                String exp = "(" + infixStack.get(infixStack.size()-2) + str + infixStack.get(infixStack.size()-1) + ")";
                infixStack.remove(infixStack.size() - 1);
                infixStack.remove(infixStack.size() - 1);
                infixStack.add(exp);
            } else {
                infixStack.add(str);
            }
        }
        return String.join("", infixStack);
    }
    private String getCleanedCalc(String calc){
        ArrayList<String> entryStack = new ArrayList( Arrays.asList(calc.trim().split("\\s+")));
        entryStack.replaceAll(s-> s.replace("%", "#"));
        entryStack.replaceAll(s-> s.replace("√", "sqrt"));
        entryStack.replaceAll(s-> s.replace("π", "pi"));
        switch (nMode) {
            case Prefix:
                return prefixToInfix(entryStack);
            case Postfix:
                return postfixToInfix(entryStack);
            default:
                return String.join("", entryStack);
        }
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
        if (isResult) {
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
        alphaNumUpdate();
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
        if (!entryStr.equals("")) {
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
        history.add(entryStr);
        String calcStr = getCleanedCalc(entryStr);

        Expression e = new Expression(calcStr);
        display.setText("");
        double r = e.calculate();
        if ((r % 1) == 0) {
            entryStr = String.valueOf((long) r);
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