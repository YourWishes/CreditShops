package com.domsplace.CreditShops.Enums;

import com.domsplace.CreditShops.Bases.DomsEnum;

public class ManagerType extends DomsEnum {
    public static final ManagerType CONFIG = new ManagerType("Configuration");
    public static final ManagerType PLUGIN = new ManagerType("Plugin");
    
    //Instance
    private String type;
    
    public ManagerType(String type) {
        this.type = type;
    }
    
    public String getType() {
        return this.type;
    }
}
