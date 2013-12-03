package com.domsplace.CreditShops.Enums;

import com.domsplace.CreditShops.Bases.DomsEnum;

public class ManagerType extends DomsEnum {
    public static final ManagerType CONFIG = new ManagerType("Configuration");
    public static final ManagerType PLUGIN = new ManagerType("Plugin");
    public static final ManagerType WORTH = new ManagerType("Worth");
    public static final ManagerType BUYABLE = new ManagerType("Buyable");
    public static final ManagerType SELLABLE = new ManagerType("Sellable");
    public static final ManagerType CRAFT_BUKKIT = new ManagerType("Craftbukkit");
    public static final ManagerType SHOP = new ManagerType("Shop");
    
    //Instance
    private String type;
    
    public ManagerType(String type) {
        this.type = type;
    }
    
    public String getType() {
        return this.type;
    }
}
