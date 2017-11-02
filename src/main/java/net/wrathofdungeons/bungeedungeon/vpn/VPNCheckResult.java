package net.wrathofdungeons.bungeedungeon.vpn;

public class VPNCheckResult {
    private String ip;
    private boolean isVPN;
    private double result;

    public VPNCheckResult(String ip, boolean isVPN, double result){
        this.ip = ip;
        this.isVPN = isVPN;
        this.result = result;
    }

    public String getIP(){
        return this.ip;
    }

    public boolean isVPN(){
        return this.isVPN;
    }

    public double getResult(){
        return this.result;
    }
}
