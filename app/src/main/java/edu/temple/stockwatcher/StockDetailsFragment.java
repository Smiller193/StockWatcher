package edu.temple.stockwatcher;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Logger;


public class StockDetailsFragment extends Fragment {

    View v;
    ImageView graphImageView;
    TextView companyName;
    TextView stockPrice;
    String price;
    String baseURL = "http://dev.markitondemand.com/MODApis/Api/v2/Quote/json/?symbol=";
    Logger log = Logger.getAnonymousLogger();


    public StockDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_stock_details, container, false);

        graphImageView = (ImageView) v.findViewById(R.id.graph);
        companyName = (TextView) v.findViewById(R.id.stock_name);
        stockPrice = (TextView) v.findViewById(R.id.stock_price);

        return v;
    }

    public void showGraph(Stock stock) { //show graph
        Picasso.with(graphImageView.getContext()).load("https://chart.yahoo.com/z?t=1d&s="+stock.getSymbol()).into(graphImageView);
    }

    public void showCompanyName(Stock stock) {
        companyName.setText(String.valueOf(stock.getCompanyName()));
    }

    public void showStockPrice(Stock stock) {
        stockPrice.setText(String.valueOf(stock.getPrice()));

    }

    public void showStockInfo(Stock stock) {
        //showCompanyName(stock);
        showGraph(stock);
        //showStockPrice(stock);
        retrieveStockPrice(stock);
    }

    public void retrieveStockPrice(Stock stock){
        log.info("retrieveStockPriceData");
        final String urlString = "http://dev.markitondemand.com/MODApis/Api/v2/Quote/json/?symbol=" + stock.getSymbol();
        Thread t = new Thread(){
            @Override
            public void run(){
                log.info("run() is called");
                try {
                    URL url = new URL(urlString);
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(
                                    url.openStream()));
                    String tmpString = "";
                    String response = "";
                    while (tmpString != null) {
                        response.concat(tmpString);
                        response = response + tmpString;
                        tmpString = reader.readLine();
                    }
                    Message msg = Message.obtain();
                    msg.obj = response;

                    Log.d("downloaded data", response);
                    responseHandler.sendMessage(msg);
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    Handler responseHandler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg){
            try {
                JSONObject blockObject = new JSONObject((String) msg.obj);
                String name = blockObject.getString("Name");
                String price = blockObject.getString("LastPrice");

                companyName.setText(name);
                stockPrice.setText(price);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }
    });



}
