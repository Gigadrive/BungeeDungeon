package net.wrathofdungeons.bungeedungeon.users;

import net.md_5.bungee.api.ChatColor;

public enum Rank {
    ADMIN(5,"Admin", ChatColor.DARK_RED),
    GM(4,"Game Master",ChatColor.RED),
    MODERATOR(3,"Moderator",ChatColor.GOLD),
    DONATOR(2,"Donator",ChatColor.DARK_GREEN),
    USER(1,"User",ChatColor.WHITE);

    private int id;
    private String name;
    private ChatColor color;

    Rank(int id, String name, ChatColor color){
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public int getID(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public ChatColor getColor(){
        return this.color;
    }

    public static Rank fromTag(String t){
        for(Rank r : values()) if(r.toString().equalsIgnoreCase(t)) return r;

        return Rank.USER;
    }

    public static Rank fromID(int i){
        for(Rank r : values()) if(r.getID() == i) return r;

        return Rank.USER;
    }
}
