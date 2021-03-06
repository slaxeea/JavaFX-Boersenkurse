package ch.floundsimon.ch.boerse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author kappe
 */
public class CryptoData {

    private static boolean alreadyGotPrice = false;
    public static double eth, doge, btc = 0;
    public static ArrayList<Double> btcArray = new ArrayList<>();
    public static String latestQueryCurrency = "none";

    public static void getData(String currency) throws Exception {
        if (!alreadyGotPrice || currency != latestQueryCurrency) {

            latestQueryCurrency = currency;
            String uri = "https://api.coingecko.com/api/v3/simple/price?ids=bitcoin%2Cethereum%2Cdogecoin&vs_currencies=" + currency + "";
            
            String response_content = DataHelper.makeApiCall(uri);
            
            JSONObject object = DataHelper.getJSONObject(response_content);

            JSONObject priceEth = (JSONObject) object.get("ethereum");
            JSONObject priceBtc = (JSONObject) object.get("bitcoin");
            JSONObject priceDoge = (JSONObject) object.get("dogecoin");

            Object btcObject = priceBtc.get(currency.toLowerCase());
            String btcString = String.valueOf(btcObject);
            btc = Double.valueOf(btcString);

            Object ethObject = priceEth.get(currency.toLowerCase());
            String ethString = String.valueOf(ethObject);
            eth = Double.valueOf(ethString);

            Object dogeObject = priceDoge.get(currency.toLowerCase());
            String dogeString = String.valueOf(dogeObject);
            doge = Double.valueOf(dogeString);

            alreadyGotPrice = true;
        }
    }

    public static Double[] getFiveDays(Coins coin, String currency) throws Exception {
        Double array[] = new Double[5];
        LocalDate td = LocalDate.now();
        String format = "dd-MM-yyyy";
        String date = td.format(DateTimeFormatter.ofPattern(format));
        String uri;

        for (int i = 0; i < 5; i++) {
            uri = "https://api.coingecko.com/api/v3/coins/" + coin.toString(coin) + "/history?date=" + td.minusDays(i).format(DateTimeFormatter.ofPattern(format)) + "&localization=false";

            String response_content = DataHelper.makeApiCall(uri);

            JSONObject object = DataHelper.getJSONObject(response_content);
            JSONObject market_data = (JSONObject) object.get("market_data");
            JSONObject current_price = (JSONObject) market_data.get("current_price");

            Object price = current_price.get(currency.toLowerCase());
            String priceString = String.valueOf(price);
            Double fin = Double.valueOf(priceString);

            array[i] = fin;
        }
        return array;
    }

    public static Double getCoin(Coins coin, String currency) throws Exception {
        getData(currency);
        switch (coin) {
            case BITCOIN:
                return btc;
            case ETHEREUM:
                return eth;
            case DOGECOIN:
                return doge;
        }
        return null;
    }
}
