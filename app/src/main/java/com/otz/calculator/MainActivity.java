package com.otz.calculator;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.otz.calculator.databinding.ActivityMainBinding;

import org.apache.commons.lang3.StringUtils;
import org.mariuszgromada.math.mxparser.Expression;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    enum Notation {
        Prefix, Infix, Postfix
    }
    enum Base {
        Binary, Octal, Decimal, Duodecimal, Hexadecimal
    }

    Notation nMode = Notation.Infix;
    Base bMode = Base.Decimal;
    private EditText display;
    private String entryStr = "";
    ArrayList<String> history = new ArrayList<String>();
    private boolean isResult = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        display = findViewById(R.id.input);
        enforceNotationButtons();
        enforceBaseButtons();
        display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getString(R.string.display).equals(display.getText().toString())){
                    display.setText("");
                }
            }
        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_theme, R.id.nav_history)
                .setOpenableLayout(drawer)
                .build(); //R.id.nav_notation, R.id.nav_base
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void enforceNotationButtons(){
        Button sqrt = findViewById(R.id.sqrootBTN);
        Button paran = findViewById(R.id.parenthesesBTN);
        if (nMode == Notation.Infix) {
            sqrt.setEnabled(true);
            sqrt.setBackgroundResource(R.drawable.buttonenabled);
            paran.setEnabled(true);
            paran.setBackgroundResource(R.drawable.buttonenabled);
        } else {
            sqrt.setEnabled(false);
            sqrt.setBackgroundResource(R.drawable.buttondisabled);
            paran.setEnabled(false);
            paran.setBackgroundResource(R.drawable.buttondisabled);
        }
    }
    private void enforceBaseButtons(){
        switch (bMode){
            case Binary:
                disableBinary();
                break;
            case Octal:
                disableOctal();
                enableOctal();
                break;
            case Decimal:
                disableDecimal();
                enableDecimal();
                break;
            case Duodecimal:
                disableDuodecimal();
                enableDuodecimal();
                break;
            case Hexadecimal:
                enableHexadecimal();
                break;
        }
    }
    private void enableOctal(){
        Button b2 = findViewById(R.id.twoBTN);
        Button b3 = findViewById(R.id.threeBTN);
        Button b4 = findViewById(R.id.fourBTN);
        Button b5 = findViewById(R.id.fiveBTN);
        Button b6 = findViewById(R.id.sixBTN);
        Button b7 = findViewById(R.id.sevenBTN);

        b2.setEnabled(true);
        b2.setBackgroundResource(R.drawable.buttonenabled);
        b3.setEnabled(true);
        b3.setBackgroundResource(R.drawable.buttonenabled);
        b4.setEnabled(true);
        b4.setBackgroundResource(R.drawable.buttonenabled);
        b5.setEnabled(true);
        b5.setBackgroundResource(R.drawable.buttonenabled);
        b6.setEnabled(true);
        b6.setBackgroundResource(R.drawable.buttonenabled);
        b7.setEnabled(true);
        b7.setBackgroundResource(R.drawable.buttonenabled);

    }
    private void enableDecimal(){
        enableOctal();
        Button b8 = findViewById(R.id.eightBTN);
        Button b9 = findViewById(R.id.nineBTN);

        b8.setEnabled(true);
        b8.setBackgroundResource(R.drawable.buttonenabled);
        b9.setEnabled(true);
        b9.setBackgroundResource(R.drawable.buttonenabled);
    }
    private void enableDuodecimal(){
        enableDecimal();
        Button bA = findViewById(R.id.aBTN);
        Button bB = findViewById(R.id.bBTN);

        bA.setEnabled(true);
        bA.setBackgroundResource(R.drawable.buttonenabled);
        bB.setEnabled(true);
        bB.setBackgroundResource(R.drawable.buttonenabled);
    }
    private void enableHexadecimal(){
        enableDuodecimal();
        Button bC = findViewById(R.id.cBTN);
        Button bD = findViewById(R.id.dBTN);
        Button bE = findViewById(R.id.eBTN);
        Button bF = findViewById(R.id.fBTN);

        bC.setEnabled(true);
        bC.setBackgroundResource(R.drawable.buttonenabled);
        bD.setEnabled(true);
        bD.setBackgroundResource(R.drawable.buttonenabled);
        bE.setEnabled(true);
        bE.setBackgroundResource(R.drawable.buttonenabled);
        bF.setEnabled(true);
        bF.setBackgroundResource(R.drawable.buttonenabled);
    }
    private void disableBinary(){
        disableOctal();
        Button b2 = findViewById(R.id.twoBTN);
        Button b3 = findViewById(R.id.threeBTN);
        Button b4 = findViewById(R.id.fourBTN);
        Button b5 = findViewById(R.id.fiveBTN);
        Button b6 = findViewById(R.id.sixBTN);
        Button b7 = findViewById(R.id.sevenBTN);

        b2.setEnabled(false);
        b2.setBackgroundResource(R.drawable.buttondisabled);
        b3.setEnabled(false);
        b3.setBackgroundResource(R.drawable.buttondisabled);
        b4.setEnabled(false);
        b4.setBackgroundResource(R.drawable.buttondisabled);
        b5.setEnabled(false);
        b5.setBackgroundResource(R.drawable.buttondisabled);
        b6.setEnabled(false);
        b6.setBackgroundResource(R.drawable.buttondisabled);
        b7.setEnabled(false);
        b7.setBackgroundResource(R.drawable.buttondisabled);
    }
    private void disableOctal(){
        disableDecimal();
        Button b8 = findViewById(R.id.eightBTN);
        Button b9 = findViewById(R.id.nineBTN);
        b8.setEnabled(false);
        b8.setBackgroundResource(R.drawable.buttondisabled);
        b9.setEnabled(false);
        b9.setBackgroundResource(R.drawable.buttondisabled);
    }
    private void disableDecimal(){
        disableDuodecimal();
        Button bA = findViewById(R.id.aBTN);
        Button bB = findViewById(R.id.bBTN);
        bA.setEnabled(false);
        bA.setBackgroundResource(R.drawable.buttondisabled);
        bB.setEnabled(false);
        bB.setBackgroundResource(R.drawable.buttondisabled);
    }
    private void disableDuodecimal(){
        Button bC = findViewById(R.id.cBTN);
        Button bD = findViewById(R.id.dBTN);
        Button bE = findViewById(R.id.eBTN);
        Button bF = findViewById(R.id.fBTN);
        bC.setEnabled(false);
        bC.setBackgroundResource(R.drawable.buttondisabled);
        bD.setEnabled(false);
        bD.setBackgroundResource(R.drawable.buttondisabled);
        bE.setEnabled(false);
        bE.setBackgroundResource(R.drawable.buttondisabled);
        bF.setEnabled(false);
        bF.setBackgroundResource(R.drawable.buttondisabled);
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