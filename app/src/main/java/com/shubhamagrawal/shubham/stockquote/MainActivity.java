package com.shubhamagrawal.shubham.stockquote;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.net.URI;
import java.net.URL;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {


    public final static  String STOCK_SYMBOL = "com.example.stockquote.STOCK";

    private SharedPreferences stockSymbolsEntered;

    private TableLayout stockTableScrollView;

    private EditText stockSymbolEditText;

    Button enterStockSymbolButton;

    Button deleteStocksButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stockSymbolsEntered = getSharedPreferences("stockList", MODE_PRIVATE);

        stockTableScrollView = (TableLayout) findViewById(R.id.stockTableScrollView);

        stockSymbolEditText = (EditText) findViewById(R.id.stockSymbolEditText);

        enterStockSymbolButton = (Button) findViewById(R.id.enterStockSymbolButton);

        deleteStocksButton = (Button) findViewById(R.id.deleteStockButton);

        enterStockSymbolButton.setOnClickListener(enterStockButtonListener);
        deleteStocksButton.setOnClickListener(deleteStockButtonListener);

        updateSavedStockList(null);

    }

    public View.OnClickListener deleteStockButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            deleteAllStocks();

            SharedPreferences.Editor preferenceEditor = stockSymbolsEntered.edit();

            preferenceEditor.clear();
            preferenceEditor.apply();
        }
    };

    private void updateSavedStockList(String newStockSymbol)
    {
        String[] stocks = stockSymbolsEntered.getAll().keySet().toArray(new String[0]);

        Arrays.sort(stocks, String.CASE_INSENSITIVE_ORDER);

        if(newStockSymbol!=null)
        {
            insertStockInScrollView(newStockSymbol, Arrays.binarySearch(stocks, newStockSymbol));
        }
        else
        {
            for(int i=0; i<stocks.length; i++)
            {
                insertStockInScrollView(stocks[i], i);
            }
        }
    }


    private void insertStockInScrollView(String stock, int arrayIndex)
    {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View newStockRow = inflater.inflate(R.layout.stock_quote_row, null);

        TextView newStockTextView = (TextView) newStockRow.findViewById(R.id.stockSymbolTextView);

        newStockTextView.setText(stock);

        Button stockQuoteButton = (Button) newStockRow.findViewById(R.id.stockQuoteButton);

        Button quoteFromWebButton = (Button) newStockRow.findViewById(R.id.quoteFromWeButton);

        stockQuoteButton.setOnClickListener(getStockActivityListener);

        quoteFromWebButton.setOnClickListener(getStockFromWebsiteListener);

        stockTableScrollView.addView(newStockRow, arrayIndex);

    }


    public View.OnClickListener getStockFromWebsiteListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TableRow tableRow = (TableRow) v.getParent();

            TextView stockTextView = (TextView) tableRow.findViewById(R.id.stockSymbolTextView);

            String stockSymbol = stockTextView.getText().toString();

            String stockURL = getString(R.string.yahoo_stock_url) + stockSymbol;

            Intent getStockWebPage = new Intent(Intent.ACTION_VIEW, Uri.parse(stockURL));

            startActivity(getStockWebPage);
        }
    };

    public View.OnClickListener getStockActivityListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            TableRow tableRow = (TableRow) v.getParent();

            TextView stockTextView = (TextView) tableRow.findViewById(R.id.stockSymbolTextView);

            String stockSymbol = stockTextView.getText().toString();

            Intent intent = new Intent(MainActivity.this, StartInfoActivity.class);

            intent.putExtra(STOCK_SYMBOL, stockSymbol);

            startActivity(intent);
        }
    };

    public Button.OnClickListener enterStockButtonListener = new Button.OnClickListener()
    {

        @Override
        public void onClick(View v) {
            if(stockSymbolEditText.getText().length()>0)
            {
                saveStockSymbol(stockSymbolEditText.getText().toString());

                stockSymbolEditText.setText("");

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                imm.hideSoftInputFromWindow(stockSymbolEditText.getWindowToken(), 0);
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle(R.string.invalid_stock_symbol);

                builder.setPositiveButton(R.string.ok, null);

                builder.setMessage(R.string.missing_stock_symbol);

                AlertDialog theAlertDialog = builder.create();
                theAlertDialog.show();
            }
        }
    };

    private void saveStockSymbol(String newStock)
    {
        String isTheStockNew = stockSymbolsEntered.getString(newStock, null);

        SharedPreferences.Editor preferenceEditor = stockSymbolsEntered.edit();

        preferenceEditor.putString(newStock, newStock);

        preferenceEditor.apply();

        if(isTheStockNew == null)
        {
            updateSavedStockList(newStock);
        }
    }



    private void deleteAllStocks()
    {
        stockTableScrollView.removeAllViews();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
