package dede.ugurcan.dovizKuru;

/**
 * @author: Ugurcan Dede
 * @date: 21.12.2019
 * @description: Exchange converter using exchangeratesapi.io by Ugurcan Dede
 * @project-url: https://github.com/ugurcandede/DovizKuruCevirici
 */


import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {
    Button btnHistory;
    DatePickerDialog picker;
    EditText edtTextIlkDate, edtTextSonDate;
    String baslangıcTarihi, bitisTarihi;

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

        btnHistory = findViewById(R.id.historyButton);
        edtTextIlkDate = findViewById(R.id.edtTextIlkDate);
        edtTextIlkDate.setInputType(InputType.TYPE_NULL);

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

        edtTextIlkDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                picker = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                edtTextIlkDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                baslangıcTarihi = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                bitisTarihi = year + "-" + (monthOfYear + 1) + "-" + (dayOfMonth + 1);
                            }
                        }, year, month, day);
                picker.setButton(DatePickerDialog.BUTTON_POSITIVE, "Tamam", picker);
                picker.setButton(DatePickerDialog.BUTTON_NEGATIVE, "İptal", picker);
                picker.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                picker.show();
            }
        });
    }

    public void verileriGetir(View v) {
        if ("".equals(edtBazKur.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Parasal Değer Girin", Toast.LENGTH_SHORT).show();
        } else {
            DownloadData downloadData = new DownloadData();
            try {

                String url = "https://api.exchangeratesapi.io/latest?base=" + bazKurSpn.getSelectedItem().toString();
                downloadData.execute(url);
                //System.out.println("URL: " + url);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void eskiVerileriGetir(View v) {
        if ("".equals(edtTextIlkDate.getText().toString()) || "".equals(edtBazKur.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Lütfen Tarih ve Parasal Değeri Kontrol Edin", Toast.LENGTH_SHORT).show();
        } else {
            DownlodHistoryData downloadHistoryData = new DownlodHistoryData();
            try {
                String url = "https://api.exchangeratesapi.io/history?start_at=" + baslangıcTarihi + "&end_at=" + bitisTarihi + "&base=" + bazKurSpn.getSelectedItem().toString();
                downloadHistoryData.execute(url);
                //System.out.println("URL: " + url);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class DownlodHistoryData extends AsyncTask<String, Void, String> {
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
            System.out.println("VERİ: " + s);

            try {
                JSONObject ratesObject = new JSONObject(s);
                String rates = ratesObject.getString("rates");
                //System.out.println("Rates: " + rates);


                JSONObject dateObject = new JSONObject(rates);
                String date = dateObject.getString(baslangıcTarihi);
                //System.out.println("TARİH: " + date);

                JSONObject currencyObject = new JSONObject(date);
                String currency = currencyObject.getString(cevirilenKurSpn.getSelectedItem().toString());
                //System.out.println("Para Değeri: " + currency);

                Double doubleHistoryKur, doubleBazKur;
                String bazText = edtBazKur.getText().toString();
                doubleBazKur = Double.parseDouble(bazText);
                doubleHistoryKur = Double.parseDouble(currency);

                Double historyHesapla = (doubleBazKur * doubleHistoryKur);
                NumberFormat formatter = new DecimalFormat("#0.000");
                System.out.println(formatter.format(historyHesapla) + " " + cevirilenKurSpn.getSelectedItem().toString());
                textView2.setVisibility(View.VISIBLE);
                textView2.setText(baslangıcTarihi + "\nTarihinde Kur Değeri");
                txvSonuc.setText(formatter.format(historyHesapla) + " " + cevirilenKurSpn.getSelectedItem().toString());

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
            System.out.println("VERİ: " + s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String base = jsonObject.getString("base");
                //System.out.println("Ana Para Birimi: " + base);

                String rates = jsonObject.getString("rates");
                JSONObject jsonObject1 = new JSONObject(rates);
                //System.out.println("Para Birimi Kur Değeri: " + rates);

                String guncelKur = jsonObject1.getString(cevirilenKurSpn.getSelectedItem().toString());

                Double DoubleGuncelKur;
                Double DoubleBazKur;
                String bazText = edtBazKur.getText().toString();
                DoubleBazKur = Double.parseDouble(bazText);
                DoubleGuncelKur = Double.parseDouble(guncelKur);

                Double hesapla = (DoubleBazKur * DoubleGuncelKur);
                NumberFormat formatter = new DecimalFormat("#0.000");
                //System.out.println(formatter.format(hesapla) + " " + cevirilenKurSpn.getSelectedItem().toString());
                txvSonuc.setText(formatter.format(hesapla) + " " + cevirilenKurSpn.getSelectedItem().toString());
                textView2.setVisibility(View.VISIBLE);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
