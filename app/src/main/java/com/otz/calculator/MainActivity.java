package com.otz.calculator;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.otz.calculator.databinding.ActivityMainBinding;

import org.apache.commons.lang3.StringUtils;
import org.mariuszgromada.math.mxparser.Expression;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

//TODO Add Cursor on result screen
//TODO Auto scroll screen to right most side
//TODO Reorganize clear screen and update screen
//TODO Separate into different classes
//TODO Make Dialog wider
//TODO Fix XNOR
//TODO Fix after-result display manipulation
//TODO Add icons to drawer

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    enum Notation {
        Prefix, Infix, Postfix
    }
    enum Base {
        Binary, Octal, Decimal, Duodecimal, Hexadecimal
    }


    Notation nMode;
    Base bMode;

    private EditText display;
    private String entryStr;
    private Dialog dialog;
    private PopupWindow popupWindow;

    ArrayList<String> expHistory = new ArrayList<String>();
    private boolean isResult = false;
    private boolean superToggle;
    DecimalFormat df = new DecimalFormat("#.#######");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nMode = Notation.Infix;
        bMode = Base.Decimal;
        entryStr = "";
        dialog = new Dialog(MainActivity.this); //, R.style.Dialog
        superToggle = false;

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
                R.id.nav_home)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                //it's possible to do more actions on several items, if there is a large amount of items I prefer switch(){case} instead of if()
                if (id == R.id.nav_notation){
                    popup(1);
                } else if (id == R.id.nav_base){
                    popup(2);
                } else if (id == R.id.nav_history){
                    //popup(3);
                    historyDialog();
                } else if (id == R.id.nav_theme){
                    popup(4);
                }
                //This is for maintaining the behavior of the Navigation view
                //NavigationUI.onNavDestinationSelected(menuItem,navController);
                //This is for closing the drawer after acting on it
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void historyDialog() {
        dialog.show();;
        dialog.setContentView(R.layout.popup_history);
        ListView hList = dialog.findViewById(R.id.historyList);
        //dialog.setTitle("History :: Base");
        dialog.setCancelable(true);
        ArrayAdapter<String> hAdapter = new ArrayAdapter<String> (MainActivity.this,R.layout.history_item,expHistory);
        hList.setAdapter(hAdapter);
    }
    public void popup(int id) {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView;
        switch(id) {
            case 1:
                popupView = inflater.inflate(R.layout.popup_notation, null);
                break;
            case 2:
                popupView = inflater.inflate(R.layout.popup_base, null);
                break;
            case 3:
                popupView = inflater.inflate(R.layout.popup_history, null);
                break;
            default:
                popupView = inflater.inflate(R.layout.popup_theme, null);
                break;
        }

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

    }

    public void changeNotation(View v) {
        switch (v.getId()) {
            case (R.id.Pre):
                nMode = Notation.Prefix;
                enforceNotationButtons();
                break;
            case (R.id.In):
                nMode = Notation.Infix;
                enforceNotationButtons();
                break;
            case (R.id.Post):
                nMode = Notation.Postfix;
                enforceNotationButtons();
                break;
        }
        entryStr = "";
        display.setText(entryStr);
        popupWindow.dismiss();
    }
    public void changeBase(View v) {
        switch (v.getId()) {
            case (R.id.Bin):
                bMode = Base.Binary;
                enforceBaseButtons();
                break;
            case (R.id.Oct):
                bMode = Base.Octal;
                enforceBaseButtons();
                break;
            case (R.id.Dec):
                bMode = Base.Decimal;
                enforceBaseButtons();
                break;
            case (R.id.Duo):
                bMode = Base.Duodecimal;
                enforceBaseButtons();
                break;
            case (R.id.Hex):
                bMode = Base.Hexadecimal;
                enforceBaseButtons();
                break;
        }
        entryStr = "";
        display.setText(entryStr);
        popupWindow.dismiss();
    }

    private void enforceNotationButtons(){
        Button paran = findViewById(R.id.parenthesesBTN);
        if (nMode == Notation.Infix) {
            paran.setEnabled(true);
            paran.setBackgroundResource(R.drawable.buttonenabled);
        } else {
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
        Button decimalPoint = findViewById(R.id.pointBTN);
        if (bMode != Base.Decimal) {
            decimalPoint.setEnabled(false);
            decimalPoint.setBackgroundResource(R.drawable.buttondisabled);
        } else {
            decimalPoint.setEnabled(true);
            decimalPoint.setBackgroundResource(R.drawable.buttonenabled);
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

    public void historyItemBTN(View view) {
        TextView hI = (TextView) view;
        String expression = hI.getText().toString();
        dialog.dismiss();

        int posStart = expression.indexOf('[');
        int posEnd = expression.indexOf(']');
        String str = expression.substring(0, posStart);
        String base = expression.substring(posStart+1, posEnd);
        Base baseM;

        switch (base) {
            case "2":
                baseM = Base.Binary;
                break;
            case "8":
                baseM = Base.Octal;
                break;
            case "12":
                baseM = Base.Duodecimal;
                break;
            case "16":
                baseM = Base.Hexadecimal;
                break;
            default:
                baseM = Base.Decimal;
        }
        if (bMode!=baseM){
            bMode = baseM;
            enforceBaseButtons();
            String tMessage  = "Base was changed to " + base;
            Toast.makeText(MainActivity.this, tMessage,
                    Toast.LENGTH_SHORT).show();
        }

        if (str.matches(".*[=].*")) {
            entryStr = str.replaceAll("[= ]", "");
        } else {
            entryStr = str;
        }
        display.setText(entryStr);
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
            } else if (str.matches("sqrt") && i < preStack.size()-1) {
                String exp = "(" + str + "(" + infixStack.get(infixStack.size()-1) + "))";
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
        for (int i = 0; i < postStack.size(); i++) {
            String str = postStack.get(i);
            if (str.matches(".*[-+/*^%]") && postStack.size() > 2) {
                String exp = "(" + infixStack.get(infixStack.size()-2) + str + infixStack.get(infixStack.size()-1) + ")";
                infixStack.remove(infixStack.size() - 1);
                infixStack.remove(infixStack.size() - 1);
                infixStack.add(exp);
            } else if (str.matches("sqrt") && postStack.size() > 1) {
                String exp = "(" + str + "(" + infixStack.get(infixStack.size()-1) + "))";
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
        String str;
        entryStack.replaceAll(s-> s.replace("NOT", "~"));
        entryStack.replaceAll(s-> s.replace("NAND", "~&"));
        entryStack.replaceAll(s-> s.replace("AND", "&"));
        entryStack.replaceAll(s-> s.replace("XOR", "(+)"));
        entryStack.replaceAll(s-> s.replace("XNOR", "~(+)"));
        entryStack.replaceAll(s-> s.replace("NOR", "~|"));
        entryStack.replaceAll(s-> s.replace("OR", "|"));

        for (int i = 0; i < entryStack.size(); i++) {
            str = entryStack.get(i);
            if (str.matches(".*[1234567890ABCDEF].*")) {
                switch (bMode) {
                    case Binary:
                        entryStack.set(i, Integer.toString(Integer.parseInt(str, 2), 10));
                        break;
                    case Octal:
                        entryStack.set(i, Integer.toString(Integer.parseInt(str, 8), 10));
                        break;
                    case Duodecimal:
                        entryStack.set(i, Integer.toString(Integer.parseInt(str, 12), 10));
                        break;
                    case Hexadecimal:
                        entryStack.set(i, Integer.toString(Integer.parseInt(str, 16), 10));
                        break;
                }
            }
        }
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
            entryStr += " ";
            display.setText(entryStr);
            isResult = false;
        }
    }

    public void alphaNumUpdate(){
        if (isResult) {
            entryStr += " ";
            display.setText(entryStr);
            isResult = false;
        } else if (!entryStr.equals("") && (entryStr.matches(".*[-+/*√)^%]")
                || (entryStr.endsWith("NAND") || entryStr.endsWith("XNOR") || entryStr.endsWith("NaN")
                || entryStr.endsWith("AND") || entryStr.endsWith("NOR") || entryStr.endsWith("XOR")
                || entryStr.endsWith("NOT") || entryStr.endsWith("OR")))) {
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
    public void constBTN(View view){
        alphaNumUpdate();
        if (superToggle) {
            entryStr += "e";
        } else {
            entryStr += "π";
        }
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

    public void notBTN(View view){
        operatorUpdate();
        entryStr += "NOT";
        display.setText(entryStr);
    }
    public void andBTN(View view){
        operatorUpdate();
        if (superToggle) {
            entryStr += "NAND";
        } else {
            entryStr += "AND";
        }
        display.setText(entryStr);
    }
    public void orBTN(View view){
        operatorUpdate();
        if (superToggle) {
            entryStr += "NOR";
        } else {
            entryStr += "OR";
        }
        display.setText(entryStr);
    }
    public void xorBTN(View view){
        operatorUpdate();
        if (superToggle) {
            entryStr += "XNOR";
        } else {
            entryStr += "XOR";
        }
        display.setText(entryStr);
    }

    public void superBTN(View view){
        superToggle = !superToggle;
        Button bSuper = findViewById(R.id.superBTN);
        Button bConst = findViewById(R.id.constBTN);
        Button bAND = findViewById(R.id.andBTN);
        Button bOR = findViewById(R.id.orBTN);
        Button bXOR = findViewById(R.id.xorBTN);
        if (superToggle) {
            bSuper.setBackgroundResource(R.drawable.buttontoggleenabled);
            bConst.setText(R.string.eConst);
            bAND.setText(R.string.nand);
            bOR.setText(R.string.nor);
            bXOR.setText(R.string.xnor);
        } else {
            bSuper.setBackgroundResource(R.drawable.buttontoggledisabled);
            bConst.setText(R.string.pi);
            bAND.setText(R.string.and);
            bOR.setText(R.string.or);
            bXOR.setText(R.string.xor);
        }
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
        //expHistory.add(entryStr);
        String calcStr = getCleanedCalc(entryStr);

        Expression e = new Expression(calcStr);
        display.setText("");
        double r = e.calculate();
        String str = df.format(r);

        switch (bMode) {
            case Binary:
                expHistory.add(entryStr + "[2] ");
                entryStr = Integer.toString(Integer.parseInt(str, 10), 2);
                expHistory.add("= " + entryStr + " [2] ");
                break;
            case Octal:
                expHistory.add(entryStr + "[8] ");
                entryStr = Integer.toString(Integer.parseInt(str, 10), 8);
                expHistory.add("= " + entryStr + " [8] ");
                break;
            case Duodecimal:
                expHistory.add(entryStr + "[12] ");
                entryStr = (Integer.toString(Integer.parseInt(str, 10), 12)).toUpperCase();
                expHistory.add("= " + entryStr + " [12] ");
                break;
            case Hexadecimal:
                expHistory.add(entryStr + "[16] ");
                entryStr = (Integer.toString(Integer.parseInt(str, 10), 16)).toUpperCase();
                expHistory.add("= " + entryStr + " [16] ");
                break;
            default:
                expHistory.add(entryStr + "[10] ");
                entryStr = str;
                expHistory.add("= " + entryStr + " [10] ");
        }

        display.setText(entryStr);
        //expHistory.add("= " + entryStr + " ");

        isResult = true;
    }

    public void backspaceBTN(View view){
        showingResult();

        if (!entryStr.equals("")){
            if (entryStr.endsWith("NAND") || entryStr.endsWith("XNOR")) {
                entryStr = entryStr.substring(0, entryStr.length() - 4);
            } else if (entryStr.endsWith("NaN") || entryStr.endsWith("AND") || entryStr.endsWith("NOR") || entryStr.endsWith("XOR") || entryStr.endsWith("NOT")) {
                entryStr = entryStr.substring(0, entryStr.length() - 3);
            } else if (entryStr.endsWith("OR")) {
                entryStr = entryStr.substring(0, entryStr.length() - 2);
            } else {
                entryStr = entryStr.substring(0, entryStr.length() - 1);
            }
            display.setText(entryStr);
        }
    }
}