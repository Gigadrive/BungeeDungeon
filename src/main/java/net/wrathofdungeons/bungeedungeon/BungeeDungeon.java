package net.wrathofdungeons.bungeedungeon;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.wrathofdungeons.bungeedungeon.ban.BanReason;
import net.wrathofdungeons.bungeedungeon.cmd.*;
import net.wrathofdungeons.bungeedungeon.listener.*;
import net.wrathofdungeons.bungeedungeon.tasks.ReloadCachesTask;
import net.wrathofdungeons.bungeedungeon.users.BungeeUser;
import net.wrathofdungeons.bungeedungeon.users.Rank;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class BungeeDungeon extends Plugin {
    private static BungeeDungeon instance;
    private Configuration config;
    private static MotdManager motdManager;

    public static boolean ENABLE_LOADING_SCREEN_REMOVER = false;

    public static boolean WHITELIST_ENABLED = false;
    public static ArrayList<UUID> WHITELIST = new ArrayList<UUID>();

    public static ArrayList<String> ON_SERVER = new ArrayList<String>();

    public static final Gson GSON = new Gson();

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
        BanReason.init();

        registerListeners();
        registerCommands();

        ReloadCachesTask._do();

        ProxyServer.getInstance().registerChannel("BungeeCord");
    }

    private void registerListeners(){
        getProxy().getPluginManager().registerListener(this, new ConnectListener());
        getProxy().getPluginManager().registerListener(this, new LoginListener());
        getProxy().getPluginManager().registerListener(this, new PermissionListener());
        getProxy().getPluginManager().registerListener(this, new PingListener());
        getProxy().getPluginManager().registerListener(this, new PluginMessageListener());
        getProxy().getPluginManager().registerListener(this, new QuitListener());
    }

    private void registerCommands(){
        getProxy().getPluginManager().registerCommand(this,new CallGlobalLoginCommand());
        getProxy().getPluginManager().registerCommand(this,new FriendCommand());
        getProxy().getPluginManager().registerCommand(this,new GuildActionCommand());
        getProxy().getPluginManager().registerCommand(this,new HandlePunishmentCommand());
        getProxy().getPluginManager().registerCommand(this,new MessageCommand());
        getProxy().getPluginManager().registerCommand(this,new PingCommand());
        getProxy().getPluginManager().registerCommand(this,new ReloadSettingsCommand());
        getProxy().getPluginManager().registerCommand(this,new ReplyCommand());
        getProxy().getPluginManager().registerCommand(this,new ServerCommand());
        getProxy().getPluginManager().registerCommand(this,new StaffChatCommand());
        getProxy().getPluginManager().registerCommand(this,new UnbanCommand());
    }

    public static boolean isWhitelisted(UUID uuid){
        for(UUID u : WHITELIST){
            if(u.toString().equals(uuid.toString())) return true;
        }

        return false;
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

    public static void createStaffMessage(String msg, Rank rank){
        createStaffMessage(rank,msg);
    }

    public static void createStaffMessage(Rank rank, String msg){
        for(ProxiedPlayer p : BungeeDungeon.getInstance().getProxy().getPlayers()){
            if(BungeeUser.isLoaded(p)){
                BungeeUser u = BungeeUser.get(p);

                if(u.hasPermission(rank)){
                    p.sendMessage(TextComponent.fromLegacyText(msg));
                }
            }
        }

        BungeeDungeon.getInstance().getProxy().getConsole().sendMessage(TextComponent.fromLegacyText(msg));
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

    public static void sendToBukkit(Server server, String ... data) {
        sendToBukkit(server.getInfo(),data);
    }

    public static void sendToBukkit(ServerInfo server, String ... data) {
        if(data != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(stream);
            try {
                for(String s : data) {
                    out.writeUTF(s);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            server.sendData("BungeeCord", stream.toByteArray());
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
