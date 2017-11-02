package net.wrathofdungeons.bungeedungeon.vpn;

import com.vexsoftware.votifier.json.JSONException;
import com.vexsoftware.votifier.json.JSONObject;
import net.wrathofdungeons.bungeedungeon.BungeeDungeon;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class VPNCheckUtil {
    public static HashMap<String,VPNCheckResult> STORAGE = new HashMap<String,VPNCheckResult>();
    private static boolean taskStarted = false;

    public static void init(){
        if(!taskStarted){
            t();

            taskStarted = true;
        }
    }

    public static VPNCheckResult getResult(String ip){
        if(ip == null || ip.isEmpty() || ip.equals("127.0.0.1") || ip.equals("localhost")) return new VPNCheckResult(ip,false,0);

        if(STORAGE.containsKey(ip)){
            return STORAGE.get(ip);
        } else {
            try {
                String apiURL = "http://check.getipintel.net/check.php?ip=" + ip + "&contact=support@gigadrivegroup.com&format=json&flags=m";

                JSONObject obj = readJsonFromUrl(apiURL);

                String status = obj.get("status").toString();

                if(status.equalsIgnoreCase("success")){
                    String r = obj.get("result").toString();

                    Double d = Double.parseDouble(r);

                    if(d >= 0.75){
                        VPNCheckResult re = new VPNCheckResult(ip,true,d);
                        STORAGE.put(ip,re);
                        return re;
                    } else {
                        VPNCheckResult re = new VPNCheckResult(ip,false,d);
                        STORAGE.put(ip,re);
                        return re;
                    }
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }

        return null;
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    private static void t(){
        STORAGE.clear();

        BungeeDungeon.getInstance().getProxy().getScheduler().schedule(BungeeDungeon.getInstance(), new Runnable(){
            @Override
            public void run(){
                t();
            }
        }, 2, TimeUnit.HOURS);
    }
}
