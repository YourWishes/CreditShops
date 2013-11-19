package com.domsplace.CreditShops.Objects;

import com.domsplace.CreditShops.Bases.Base;
import com.domsplace.CreditShops.Exceptions.InvalidItemException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Shop {
    private static final List<Shop> SHOPS = new ArrayList<Shop>();
    
    public static Shop getBank(Inventory inv) {
        return Shop.getShop(inv.getTitle());
    }
    
    public static Shop getShop(String title) {
        for(Shop v : Shop.getShops()) {
            if(v == null) continue;
            if(v.getGUI() == null) continue;
            
            if(v.getGUI().getName().equalsIgnoreCase(title)) return v;
        }
        
        return null;
    }
    
    public static List<Shop> getShops() {return new ArrayList<Shop>(SHOPS);}
    
    //Instance
    private String name;
    private Inventory gui;
    
    public Shop(String name) {
        this.name = name;
        this.updateGUI();
    }
    
    public String getName() {return this.name;}
    public Inventory getGUI() {return this.gui;}
    
    public void setName(String name) {this.name = name; this.updateGUI();}

    public void delete() {
        this.updateGUI();
        this.gui.clear();
    }
    
    protected void updateGUI() {
        if(this.gui != null) {
            List<HumanEntity> ents = new ArrayList<HumanEntity>(this.gui.getViewers());
            for(HumanEntity e : ents) {
                if(e == null) continue;
                e.closeInventory();
            }
            
            this.gui.clear();
        }
        
        this.gui = Bukkit.createInventory(null, 54, Base.ChatImportant + this.name);
    }
    
    private void initGUI() {
        if(this.gui != null) return;
        this.updateGUI();
    }
    
    public List<DomsItem> getItemsFromInventory() {
        this.initGUI();
        List<DomsItem> items = new ArrayList<DomsItem>();
        
        for(ItemStack is : this.gui.getContents()) {
            if(is == null || is.getType() == null) continue;
            items.addAll(DomsItem.itemStackToDomsItems(is));
        }
        
        return items;
    }

    public boolean containsItems(List<DomsItem> relativeItemsCost) {
        return DomsItem.contains(this.getItemsFromInventory(), relativeItemsCost);
    }

    public void addItems(List<DomsItem> items) throws InvalidItemException {
        this.initGUI();
        try {
            List<ItemStack> is = DomsItem.toItemStackArray(items);
            for(ItemStack i : is) {
                this.gui.addItem(i);
            }
        } catch(Exception e){}
    }
    
    public void removeItems(List<DomsItem> relativeItemsCost) {
        this.initGUI();
        for(DomsItem i : relativeItemsCost) {
            this.removeItem(i);
        }
    }
    
    public void removeItem(DomsItem item) {
        this.initGUI();
        ItemStack is = null;
        for(ItemStack i : this.gui.getContents()) {
            if(i == null || i.getType() == null || i.getType().equals(Material.AIR)) continue;
            List<DomsItem> isc = DomsItem.itemStackToDomsItems(i);
            if(!DomsItem.contains(isc, item)) continue;
            is = i;
        }
        
        if(is == null) return;
        if(is.getAmount() > 1) {is.setAmount(is.getAmount()- 1); return;}
        
        this.gui.remove(is);
    }

    public void addItem(DomsItem item) throws InvalidItemException {
        List<DomsItem> items = new ArrayList<DomsItem>();
        items.add(item);
        this.addItems(items);
    }
}
