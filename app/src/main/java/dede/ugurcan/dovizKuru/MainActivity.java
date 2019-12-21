package dede.ugurcan.dovizKuru;

/**
 * @author: Ugurcan Dede
 * @date: 21.12.2019
 * @description: Exchange converter using exchangeratesapi.io by Ugurcan Dede
 * @project-url: https://github.com/ugurcandede/DovizKuruCevirici
 */


import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;


public class MainActivity extends AppCompatActivity {
    Spinner bazKurSpn, cevirilenKurSpn;
    EditText edtBazKur;
    TextView txvSonuc, textView2;
    String[] birimler = {"CAD", "HKD", "ISK", "PHP", "DKK", "HUF", "CZK", "GBP", "RON", "SEK",
            "IDR", "INR", "BRL", "RUB", "HRK", "JPY", "THB", "CHF", "EUR", "MYR", "BGN", "TRY",
            "CNY", "NOK", "NZD", "ZAR", "USD", "MXN", "SGD", "AUD", "ILS", "KRW", "PLN"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bazKurSpn = (Spinner) findViewById(R.id.spnBazKur);
        cevirilenKurSpn = (Spinner) findViewById(R.id.spnCevirilecekKur);
        edtBazKur = findViewById(R.id.edtBazKur);
        txvSonuc = findViewById(R.id.txvCevirilenKur);
        textView2 = findViewById(R.id.textView2);

        /**
         * Baz kur Spinner
         */

        String[] arrayBazKur = birimler;
        ArrayAdapter<String> bazKurAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arrayBazKur);
        bazKurAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bazKurSpn.setAdapter(bazKurAdapter);

        /**
         * Çevirilecek Kur Spinner
         */

        String[] arrayCevirilecekKur = birimler;
        ArrayAdapter<String> CevirilecekKurAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arrayCevirilecekKur);
        CevirilecekKurAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cevirilenKurSpn.setAdapter(CevirilecekKurAdapter);
    }

    public void verileriGetir(View v) {
        if ("".equals(edtBazKur.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Lüften Değer Girin", Toast.LENGTH_SHORT).show();
        } else {
            DownloadData downloadData = new DownloadData();
            try {

                String url = "https://api.exchangeratesapi.io/latest?base=" + bazKurSpn.getSelectedItem().toString();
                downloadData.execute(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private class DownloadData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            URL url;
            HttpURLConnection httpURLConnection;

            try {

                url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);

                int data = reader.read();
                while (data > 0) {
                    char character = (char) data;
                    result += character;
                    data = reader.read();
                }
                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //System.out.println("VERİ: " + s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String base = jsonObject.getString("base");
                System.out.println("Ana Para Birimi: " + base);

                String rates = jsonObject.getString("rates");
                JSONObject jsonObject1 = new JSONObject(rates);
                System.out.println("Para Birimi Kur Değeri: " + rates);

                String guncelKur = jsonObject1.getString(cevirilenKurSpn.getSelectedItem().toString());

                Double DoubleGuncelKur;
                Double DoubleBazKur;
                String bazText = edtBazKur.getText().toString();
                DoubleBazKur = Double.parseDouble(bazText);
                DoubleGuncelKur = Double.parseDouble(guncelKur);

                Double hesapla = (DoubleBazKur * DoubleGuncelKur);
                NumberFormat formatter = new DecimalFormat("#0.000");
                System.out.println(formatter.format(hesapla) + " " + cevirilenKurSpn.getSelectedItem().toString());
                txvSonuc.setText(formatter.format(hesapla) + " " + cevirilenKurSpn.getSelectedItem().toString());
                textView2.setVisibility(View.VISIBLE);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
