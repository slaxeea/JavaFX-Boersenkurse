package ch.floundsimon.ch.boerse;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.*;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

public class StocksData {

    public static String name = "";
    public static String currency = "";
    public static String weblink = "";
    public static String jsonString = "";

    public static Double getStock(String stock) throws Exception {

        String uri = "https://finnhub.io/api/v1/quote?symbol=" + stock + "&token=sandbox_c0u914748v6qqphtv320";
        jsonString = DataHelper.makeApiCall(uri);
        getProfile(stock);

        return writeStockJson();
    }

    private static Double writeStockJson() throws Exception {
        return getJsonValue("c", "stock.json");
    }

    public static Double writeStockJsonPc() throws Exception {
        return getJsonValue("o", "stock.json");

    }

    public static Double getHighOfTheDay() throws Exception {
        return getJsonValue("h", "stock.json");
    }

    public static Double getLowOfTheDay() throws Exception {
        return getJsonValue("l", "stock.json");
    }

    public static Double getPreviousClose() throws Exception {
        return getJsonValue("pc", "stock.json");
    }

    public static Double getJsonValue(String field, String path) throws Exception {
        JSONObject object = DataHelper.getJSONObject(jsonString);
        Object a = object.get(field);

        String priceStr = String.valueOf(a);
        Double price = 0.0;

        try {
            price = Double.valueOf(priceStr);

        } catch (Exception e) {
            System.out.println(e);
        }

        return price;
    }

    public static String getRecomendation(String symbol) throws Exception {

        String uri = "https://finnhub.io/api/v1/stock/recommendation?symbol=" + symbol + "&token=sandbox_c0u914748v6qqphtv320";

        String response_content = DataHelper.makeApiCall(uri);

        Double[] recomendationArray = new Double[6];

        JSONParser parser = new JSONParser();
        JSONArray array = (JSONArray) parser.parse(response_content);

        JSONObject o = (JSONObject) array.get(0);

        Object a = o.get("strongBuy");
        String f = String.valueOf(a);
        recomendationArray[0] = Double.valueOf(f);

        Object b = o.get("buy");
        String g = String.valueOf(b);
        recomendationArray[1] = Double.valueOf(g);

        Object c = o.get("hold");
        String h = String.valueOf(c);
        recomendationArray[2] = Double.valueOf(h);

        Object d = o.get("sell");
        String i = String.valueOf(d);
        recomendationArray[3] = Double.valueOf(i);

        Object e = o.get("strongSell");
        String j = String.valueOf(e);
        recomendationArray[4] = Double.valueOf(j);

        Double max = 0.0;
        for (int y = 0; y < 5; y++) {
            if (recomendationArray[y] > max) {
                max = recomendationArray[y];
            }
        }

        if (max == recomendationArray[0]) {
            return "We strongly recomend you buy";
        } else if (max == recomendationArray[1]) {
            return "We recomend you buy";
        } else if (max == recomendationArray[2]) {
            return "We recomend you hold";
        } else if (max == recomendationArray[3]) {
            return "We recomend you sell";
        } else if (max == recomendationArray[4]) {
            return "We strongly recomend you sell";
        } else {
            return "";
        }
    }

    public static void getProfile(String symbol) throws Exception {
        String uri = "https://finnhub.io/api/v1/stock/profile2?symbol=" + symbol + "&token=c0u914748v6qqphtv31g";
        String response_content = DataHelper.makeApiCall(uri);

        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(response_content);

        name = (String) object.get("name");
        weblink = (String) object.get("weburl");
        currency = (String) object.get("currency");
    }

    public static String getLogoPath(String symbol) throws URISyntaxException, IOException, ParseException, org.json.simple.parser.ParseException {
        String path = "https://finnhub.io/api/logo?symbol=" + symbol + "";
        return path;
    }
}
