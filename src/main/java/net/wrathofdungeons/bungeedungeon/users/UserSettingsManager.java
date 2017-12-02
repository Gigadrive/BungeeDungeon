package net.wrathofdungeons.bungeedungeon.users;

public class UserSettingsManager {
    private boolean friendRequests = true;
    private boolean privateMessages = true;
    private boolean partyRequests = true;
    private boolean guildRequests = true;
    private boolean duelRequests = true;
    private boolean tradeRequests = true;
    private boolean showDamageIndicators = true;
    private boolean showBlood = true;
    private boolean killSound = true;

    public void setFriendRequests(boolean friendRequests) {
        this.friendRequests = friendRequests;
    }

    public boolean allowsFriendRequests() {
        return friendRequests;
    }

    public void setPrivateMessages(boolean privateMessages) {
        this.privateMessages = privateMessages;
    }

    public boolean allowsPrivateMessages() {
        return privateMessages;
    }

    public void setPartyRequests(boolean partyRequests) {
        this.partyRequests = partyRequests;
    }

    public boolean allowsPartyRequests() {
        return partyRequests;
    }

    public void setGuildRequests(boolean guildRequests) {
        this.guildRequests = guildRequests;
    }

    public boolean allowsGuildRequests() {
        return guildRequests;
    }

    public void setDuelRequests(boolean duelRequests) {
        this.duelRequests = duelRequests;
    }

    public boolean allowsDuelRequests() {
        return duelRequests;
    }

    public void setTradeRequests(boolean tradeRequests) {
        this.tradeRequests = tradeRequests;
    }

    public boolean allowsTradeRequests() {
        return tradeRequests;
    }

    public void setShowBlood(boolean showBlood) {
        this.showBlood = showBlood;
    }

    public boolean mayShowBlood() {
        return showBlood;
    }

    public void setShowDamageIndicators(boolean showDamageIndicators) {
        this.showDamageIndicators = showDamageIndicators;
    }

    public boolean mayShowDamageIndicators() {
        return showDamageIndicators;
    }

    public boolean playKillSound() {
        return killSound;
    }

    public void setPlayKillSound(boolean killSound) {
        this.killSound = killSound;
    }
}
