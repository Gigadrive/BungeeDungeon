package net.wrathofdungeons.bungeedungeon;

import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.wrathofdungeons.bungeedungeon.listener.PingListener;
import net.wrathofdungeons.bungeedungeon.tasks.ReloadCachesTask;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class BungeeDungeon extends Plugin {
    private static BungeeDungeon instance;
    private Configuration config;
    private static MotdManager motdManager;

    public void onEnable(){
        instance = this;
        motdManager = new MotdManager();
        saveDefaultConfig();

        try {
            reloadConfig();
        } catch(Exception e){
            e.printStackTrace();
        }

        motdManager.loadFromDatabase();

        registerListeners();
        registerCommands();

        ReloadCachesTask._do();
    }

    private void registerListeners(){
        getProxy().getPluginManager().registerListener(this, new PingListener());
    }

    private void registerCommands(){

    }

    public void onDisable(){
        MySQLManager.getInstance().unload();
    }

    public static MotdManager getMotdManager(){
        return motdManager;
    }

    public static BungeeDungeon getInstance() {
        return instance;
    }

    public static void async(Runnable runnable){
        BungeeDungeon.getInstance().getProxy().getScheduler().runAsync(BungeeDungeon.getInstance(),runnable);
    }

    public void reloadConfig() throws IOException {
        this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        MySQLManager.getInstance().loadDataFromConfig();
    }

    public void saveConfig(){
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(getConfig(), new File(getDataFolder() + "config.yml"));
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public Configuration getConfig(){
        return this.config;
    }

    public static int randomInteger(int min, int max){
        Random rdm = new Random();
        int rdmNm = rdm.nextInt((max - min) + 1) + min;

        return rdmNm;
    }

    public static String limitString(String s, int limit){
        if(s.length() > limit){
            return s.substring(0,limit-1);
        } else {
            return s;
        }
    }

    public boolean isValidInteger(String s){
        try {
            Integer.parseInt(s);
            return true;
        } catch(Exception e){
            return false;
        }
    }

    public boolean convertIntegerToBoolean(int i){
        if(i == 0){
            return false;
        } else {
            return true;
        }
    }

    public int convertBooleanToInteger(boolean b){
        if(b){
            return 1;
        } else {
            return 0;
        }
    }

    public void saveDefaultConfig(){
        if(!getDataFolder().exists()){
            getDataFolder().mkdir();
        }

        File configFile = new File(getDataFolder(), "config.yml");
        if(!configFile.exists()){
            try {
                configFile.createNewFile();
                try (InputStream is = getResourceAsStream("config.yml");
                     OutputStream os = new FileOutputStream(configFile)) {
                    ByteStreams.copy(is, os);
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void executeConsoleCommand(String cmd){
        BungeeDungeon.getInstance().getProxy().getPluginManager().dispatchCommand(BungeeDungeon.getInstance().getProxy().getConsole(),cmd);
    }

    public static String executeShellCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();

    }
}
