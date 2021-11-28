package com.otz.calculator;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
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

//TODO (DROPPED) Add Cursor on result screen
//TODO Separate into different classes
//TODO Add item item on actionBar to show Notation

//NOTE Wrong expressions may still produce an answer

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    enum Notation {
        Prefix, Infix, Postfix
    }
    enum Base {
        Binary, Octal, Decimal, Duodecimal, Hexadecimal
    }
    enum Theme {
        Light, Dark, Space, Flower, Wooden
    }
    Notation nMode;
    Base bMode;
    Theme tMode;

    private EditText display;
    private Dialog dialog;
    private PopupWindow popupWindow;

    ArrayList<String> expHistory = new ArrayList<>();
    private boolean isResult;
    private boolean superToggle;
    DecimalFormat df = new DecimalFormat("#.#######");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nMode = Notation.Infix;
        bMode = Base.Decimal;
        tMode = Theme.Light;

        dialog = new Dialog(MainActivity.this); //, R.style.Dialog
        isResult = false;
        superToggle = false;

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);

        display = findViewById(R.id.input);
        display.setShowSoftInputOnFocus(false);
        clearDisplay();
        enforceNotationButtons();
        enforceBaseButtons();

        display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getString(R.string.display).equals(getDisplayText())){
                    clearDisplay();
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

    private String getDisplayText(){
        return display.getText().toString();
    }
    private void updateDisplay(String strToAdd){
        String displayStr = display.getText().toString();
        int cursorPos = display.getSelectionStart();
        /*String leftStr = displayStr.substring(0,cursorPos);
        String rightStr = displayStr.substring(cursorPos);
        display.setText(R.string.display);
        display.setText(String.format("%s%s%s", leftStr, strToAdd, rightStr));*/
        display.setText(String.format("%s%s", displayStr, strToAdd));
        display.setSelection(cursorPos+strToAdd.length());
    }
    private void clearDisplay() {
        display.setText("");
    }

    public void historyDialog() {
        dialog.show();
        dialog.setContentView(R.layout.popup_history);
        ListView hList = dialog.findViewById(R.id.historyList);
        dialog.setCancelable(true);
        ArrayAdapter<String> hAdapter = new ArrayAdapter<> (MainActivity.this,R.layout.history_item,expHistory);
        hList.setAdapter(hAdapter);
        int width = (int)(getResources().getDisplayMetrics().widthPixels);
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setLayout(width, height);
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
        clearDisplay();
        popupWindow.dismiss();
    }
    public void changeTheme(View v) {
        switch (v.getId()) {
            case (R.id.BtnLight):
                tMode = Theme.Light;
                enforceThemeButtons();
                break;
            case (R.id.BtnDark):
                tMode = Theme.Dark;
                enforceThemeButtons();
                break;
            case (R.id.BtnSpace):
                tMode = Theme.Space;
                enforceThemeButtons();
                break;
            case (R.id.BtnFlower):
                tMode = Theme.Flower;
                enforceThemeButtons();
                break;
            case (R.id.BtnWooden):
                tMode = Theme.Wooden;
                enforceThemeButtons();
                break;
        }
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
        clearDisplay();
        popupWindow.dismiss();
    }

    private void enforceNotationButtons(){
        Button paren = findViewById(R.id.parenthesesBTN);
        if (nMode == Notation.Infix) {
            paren.setEnabled(true);
            paren.setBackgroundResource(R.drawable.buttonenabled);
        } else {
            paren.setEnabled(false);
            paren.setBackgroundResource(R.drawable.buttondisabled);
        }
    }
    private void enforceThemeButtons(){
        int currenttheme = 0;
        if (tMode == Theme.Light) {
            currenttheme = AppCompatDelegate.MODE_NIGHT_NO;

        } else if (tMode == Theme.Dark) {
            currenttheme = AppCompatDelegate.MODE_NIGHT_YES;
        }
        AppCompatDelegate.setDefaultNightMode(currenttheme);
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
        String displayStr;
        dialog.dismiss();

        int posStart = expression.indexOf('[');
        int posEnd = expression.indexOf(']');
        String str = expression.substring(0, posStart);
        String base = expression.substring(posStart+1, posEnd);
        String tMessage = "";
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
            tMessage += "Base was changed to " + base;
        }

        posStart = expression.lastIndexOf('[');
        posEnd = expression.lastIndexOf(']');
        String note = expression.substring(posStart+1, posEnd);

        Notation noteM;
        switch (note) {
            case "Pr":
                noteM = Notation.Prefix;
                note = "Prefix";
                break;
            case "Po":
                noteM = Notation.Postfix;
                note = "Postfix";
                break;
            default:
                noteM = Notation.Infix;
                note = "Infix";
        }
        if (nMode!=noteM){
            nMode = noteM;
            enforceNotationButtons();
            if (tMessage.equals("")){
                tMessage += "Notation was changed to " + note;
            } else {
                tMessage += "\nNotation was changed to " + note;
            }
        }

        if (!tMessage.equals("")){
            Toast.makeText(MainActivity.this, tMessage,
                    Toast.LENGTH_SHORT).show();
        }

        if (str.matches(".*[=].*")) {
            displayStr = str.replaceAll("[= ]", "");
        } else {
            displayStr = str;
        }
        clearDisplay();
        updateDisplay(displayStr);
    }

    private String prefixToInfix(ArrayList<String> preStack){
        ArrayList<String> infixStack = new ArrayList<>();
        String str, exp;
        for (int i = preStack.size()-1; i >= 0 ; i--) {
            str = preStack.get(i);
            if ((str.matches(".*[-+/*^%&|]") || str.equals("~&") || str.equals("~|")
                    || str.equals("(+)")) && i < preStack.size()-2) {
                exp = "(" + infixStack.get(infixStack.size()-1) + str + infixStack.get(infixStack.size()-2) + ")";
                infixStack.remove(infixStack.size() - 1);
                infixStack.remove(infixStack.size() - 1);
                infixStack.add(exp);
            } else if ((str.matches("sqrt") || str.equals("~")) && i < preStack.size()-1) {
                exp = "(" + str + "(" + infixStack.get(infixStack.size() - 1) + "))";
                infixStack.remove(infixStack.size() - 1);
                infixStack.add(exp);
            } else if (str.equals("~(+)")) {
                exp = "(~(" + infixStack.get(infixStack.size()-1) + "(+)" + infixStack.get(infixStack.size()-2) + "))";
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
        ArrayList<String> infixStack = new ArrayList<>();
        String str, exp;
        for (int i = 0; i < postStack.size(); i++) {
            str = postStack.get(i);
            if ((str.matches(".*[-+/*^%&|]") || str.equals("~&") || str.equals("~|")
                    || str.equals("(+)")) && postStack.size() > 2 && i > 1) {
                exp = "(" + infixStack.get(infixStack.size()-2) + str + infixStack.get(infixStack.size()-1) + ")";
                infixStack.remove(infixStack.size() - 1);
                infixStack.remove(infixStack.size() - 1);
                infixStack.add(exp);
            } else if ((str.equals("sqrt") || str.equals("~")) && postStack.size() > 1) {
                exp = "(" + str + "(" + infixStack.get(infixStack.size() - 1) + "))";
                infixStack.remove(infixStack.size() - 1);
                infixStack.add(exp);
            } else if(str.equals("~(+)")) {
                exp = "(~(" + infixStack.get(infixStack.size()-2) + "(+)" + infixStack.get(infixStack.size()-1) + "))";
                infixStack.remove(infixStack.size() - 1);
                infixStack.remove(infixStack.size() - 1);
                infixStack.add(exp);
            } else {
                infixStack.add(str);
            }
        }
        return String.join("", infixStack);
    }
    private String infixSetup (ArrayList<String> infixStack){
        boolean finalizing;
        int par, pos;
        while (infixStack.contains("~(+)")) {
            ArrayList<String> newStack = new ArrayList<>();
            finalizing = false;
            par = 0;
            pos = -1;
            for (int i = 0; i < infixStack.size(); i++) {
                if (!finalizing) {
                    if (!infixStack.get(i).equals("~(+)")) {
                        newStack.add(infixStack.get(i));
                    } else {
                        pos = i;
                        infixStack.set(i,"(+)");
                        i--;
                        while (newStack.size()>0) {
                            if (newStack.get(newStack.size()-1).equals(")")) {
                                par++;
                            } else if (newStack.get(newStack.size()-1).equals("(")) {
                                par--;
                            }
                            if (newStack.get(newStack.size()-1).matches("^[0-9A-F(]+$") && par < 1){
                                newStack.remove(newStack.size() - 1);
                                i--;
                                newStack.add("(");
                                newStack.add("~");
                                newStack.add("(");
                                finalizing = true;
                                par = 0;
                                break;
                            }
                            newStack.remove(newStack.size() - 1);
                            i--;
                        }
                    }
                } else {
                    newStack.add(infixStack.get(i));
                    if (newStack.get(newStack.size()-1).equals("(")) {
                        par++;
                    } else if (newStack.get(newStack.size()-1).equals(")")) {
                        par--;
                    }
                    if (newStack.get(newStack.size()-1).matches("^[0-9A-F)]+$") && par < 1 && i > pos && pos != -1){
                        newStack.add(")");
                        newStack.add(")");
                        pos = -1;
                    }

                }
            }
            infixStack = newStack;
        }
        return String.join("", infixStack);
    }
    private String getCleanedCalc(String calc){
        ArrayList<String> entryStack = new ArrayList<>( Arrays.asList(calc.trim().split("\\s+")));
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
                return infixSetup(entryStack);
        }
    }

    //Clears entry if the entry is showing result
    public void showingResult(){
        if (isResult) {
            updateDisplay(" ");
            isResult = false;
        }
    }

    public void alphaNumUpdate(){
        String displayStr = getDisplayText();
        if (isResult) {
            clearDisplay();
            isResult = false;
        } else if (!displayStr.equals("") && (displayStr.matches(".*[-+/*√)(^%]")
                || displayStr.endsWith("NaN") || displayStr.endsWith("AND")
                || displayStr.endsWith("NOT") || displayStr.endsWith("OR"))) {
            updateDisplay(" ");
        }
    }
    public void operatorUpdate(){
        String displayStr = getDisplayText();
        if (!displayStr.equals("") && !displayStr.matches(".*[ ]")) {
            updateDisplay(" ");
        }
        if (isResult) {
            isResult = false;
        }
    }

    public void zeroBTN(View view){
        alphaNumUpdate();
        updateDisplay("0");
    }
    public void oneBTN(View view){
        alphaNumUpdate();
        updateDisplay("1");
    }
    public void twoBTN(View view){
        alphaNumUpdate();
        updateDisplay("2");
    }
    public void threeBTN(View view){
        alphaNumUpdate();
        updateDisplay("3");
    }
    public void fourBTN(View view){
        alphaNumUpdate();
        updateDisplay("4");
    }
    public void fiveBTN(View view){
        alphaNumUpdate();
        updateDisplay("5");
    }
    public void sixBTN(View view){
        alphaNumUpdate();
        updateDisplay("6");
    }
    public void sevenBTN(View view){
        alphaNumUpdate();
        updateDisplay("7");
    }
    public void eightBTN(View view){
        alphaNumUpdate();
        updateDisplay("8");
    }
    public void nineBTN(View view){
        alphaNumUpdate();
        updateDisplay("9");
    }
    public void aBTN(View view){
        alphaNumUpdate();
        updateDisplay("A");
    }
    public void bBTN(View view){
        alphaNumUpdate();
        updateDisplay("B");
    }
    public void cBTN(View view){
        alphaNumUpdate();
        updateDisplay("C");
    }
    public void dBTN(View view){
        alphaNumUpdate();
        updateDisplay("D");
    }
    public void eBTN(View view){
        alphaNumUpdate();
        updateDisplay("E");
    }
    public void fBTN(View view){
        alphaNumUpdate();
        updateDisplay("F");
    }
    public void decimalBTN(View view){
        alphaNumUpdate();
        updateDisplay(".");
    }
    public void constBTN(View view){
        alphaNumUpdate();
        if (superToggle) {
            updateDisplay("e");
        } else {
            updateDisplay("π");
        }
    }

    public void expBTN(View view){
        operatorUpdate();
        updateDisplay("^");
    }
    public void squareRootBTN(View view){
        operatorUpdate();
        updateDisplay("√");
    }

    public void multiplyBTN(View view){
        operatorUpdate();
        updateDisplay("*");
    }
    public void divideBTN(View view){
        operatorUpdate();
        updateDisplay("/");
    }
    public void addBTN(View view){
        operatorUpdate();
        updateDisplay("+");
    }
    public void subtractBTN(View view){
        operatorUpdate();
        updateDisplay("-");
    }
    public void moduloBTN(View view){
        operatorUpdate();
        updateDisplay("%");
    }

    public void notBTN(View view){
        operatorUpdate();
        updateDisplay("NOT");
    }
    public void andBTN(View view){
        operatorUpdate();
        if (superToggle) {
            updateDisplay("NAND");
        } else {
            updateDisplay("AND");
        }
    }
    public void orBTN(View view){
        operatorUpdate();
        if (superToggle) {
            updateDisplay("NOR");
        } else {
            updateDisplay("OR");
        }
    }
    public void xorBTN(View view){
        operatorUpdate();
        if (superToggle) {
            updateDisplay("XNOR");
        } else {
            updateDisplay("XOR");
        }
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
        //showingResult();
        String displayStr = getDisplayText();
        //int cursorPos = display.getSelectionStart();
        int openPar = StringUtils.countMatches(displayStr, "(");
        int closedPar = StringUtils.countMatches(displayStr, ")");

        if ((openPar == closedPar) || displayStr.matches(".*[(]")) {
            alphaNumUpdate();
            updateDisplay("(");
            //display.setSelection(cursorPos+1);
        } else if ((closedPar < openPar) && !displayStr.matches(".*[(]")) {
            if (displayStr.matches(".*[-+/*^%√]") || displayStr.endsWith("AND")
                    || displayStr.endsWith("OR") || displayStr.endsWith("NOT") ) {
                updateDisplay(" (");
            } else {
                updateDisplay(" )");
            }
            //display.setSelection(cursorPos+2);
        }
    }

    public void backspaceBTN(View view){
        showingResult();
        String displayStr = getDisplayText();
        if (!displayStr.equals("")){
            if (displayStr.endsWith("NAND") || displayStr.endsWith("XNOR")) {
                displayStr = displayStr.substring(0, displayStr.length() - 4);
            } else if (displayStr.endsWith("NaN") || displayStr.endsWith("AND") || displayStr.endsWith("NOR")
                    || displayStr.endsWith("XOR") || displayStr.endsWith("NOT")) {
                displayStr = displayStr.substring(0, displayStr.length() - 3);
            } else if (displayStr.endsWith("OR")) {
                displayStr = displayStr.substring(0, displayStr.length() - 2);
            } else {
                displayStr = displayStr.substring(0, displayStr.length() - 1);
            }
            clearDisplay();
            updateDisplay(displayStr);
        }
    }

    public void clearBTN(View view){
        clearDisplay();
    }

    public void spaceBTN(View view){
        String displayStr = getDisplayText();
        if (!displayStr.matches(".*[ ]")) {
            updateDisplay(" ");
        }
    }

    public void equalBTN(View view){
        operatorUpdate();
        String displayStr = getDisplayText();
        String calcStr = getCleanedCalc(displayStr);

        Expression e = new Expression(calcStr);
        clearDisplay();
        double r = e.calculate();
        String str = df.format(r);
        String note;
        switch (nMode) {
            case Prefix:
                note = "[Pr]";
                break;
            case Postfix:
                note = "[Po]";
                break;
            default:
                note = "[In]";
        }

        switch (bMode) {
            case Binary:
                expHistory.add(displayStr + "[2] " + note);
                displayStr = Integer.toString(Integer.parseInt(str, 10), 2);
                expHistory.add("= " + displayStr + " [2] " + note);
                break;
            case Octal:
                expHistory.add(displayStr + "[8] " + note);
                displayStr = Integer.toString(Integer.parseInt(str, 10), 8);
                expHistory.add("= " + displayStr + " [8] " + note);
                break;
            case Duodecimal:
                expHistory.add(displayStr + "[12] " + note);
                displayStr = (Integer.toString(Integer.parseInt(str, 10), 12)).toUpperCase();
                expHistory.add("= " + displayStr + " [12] " + note);
                break;
            case Hexadecimal:
                expHistory.add(displayStr + "[16] " + note);
                displayStr = (Integer.toString(Integer.parseInt(str, 10), 16)).toUpperCase();
                expHistory.add("= " + displayStr + " [16] " + note);
                break;
            default:
                expHistory.add(displayStr + "[10] " + note);
                displayStr = str;
                expHistory.add("= " + displayStr + " [10] " + note);
        }

        clearDisplay();
        updateDisplay(displayStr);
        isResult = true;
    }


}