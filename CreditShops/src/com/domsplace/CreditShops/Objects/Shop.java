package com.domsplace.CreditShops.Objects;

import com.domsplace.CreditShops.Bases.Base;
import com.domsplace.CreditShops.Bases.DataManager;
import com.domsplace.CreditShops.DataManagers.ShopManager;
import com.domsplace.CreditShops.Exceptions.InvalidItemException;
import com.domsplace.CreditShops.Objects.DomsInventoryGUI.SIZE;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Shop {
    public static final int MAX_SHOP_NAME_LENGTH = 15;
    public static final String SHOP_NAME_REGEX = "^[a-zA-Z0-9]*$";
    private static final List<Shop> SHOPS = new ArrayList<Shop>();
    public static final Shop GLOBAL_SHOP = new Shop("Server Store", null);
    
    public static List<Shop> getShops() {return new ArrayList<Shop>(SHOPS);}
    
    public static Shop getShop(String x) {
        for(Shop s : SHOPS) {
            if(s.getName().equalsIgnoreCase(x)) return s;
        }
        for(Shop s : SHOPS) {
            if(s.getName().toLowerCase().startsWith(x.toLowerCase())) return s;
        }
        for(Shop s : SHOPS) {
            if(s.getName().toLowerCase().contains(x.toLowerCase())) return s;
        }
        return null;
    }

    public static Shop getShopExact(String store) {
        for(Shop s : SHOPS) {
            if(s.getName().equalsIgnoreCase(store)) return s;
        }
        return null;
    }
    
    public static Shop getShopFromPlayer(OfflinePlayer player) {
        for(Shop s : SHOPS) {
            if(s.isOwner(player)) return s;
        }
        return null;
    }
    
    public static Shop loadShop(String filename) {
        File folder = ShopManager.STORE_FOLDER;
        if(!folder.exists()) {
            if(!folder.mkdir()) {
                return loadError("Can't create directory.", filename);
            }
        }
        
        File file = new File(folder, filename);
        if(!file.exists()) return loadError("File doesn't exist.", filename);
        
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        if(yml == null) return loadError("Failed to load YML", filename);
        
        if(!yml.contains("name")) return loadError("Failed to load Name", filename);
        //if(!yml.contains("owner")) return loadError ("Failed to load Owner", filename);
        
        String name = yml.getString("name");
        OfflinePlayer player = null;
        if(yml.contains("owner")) player = Bukkit.getOfflinePlayer(yml.getString("owner"));
        
        Shop shop;
        if(name.equalsIgnoreCase(Shop.GLOBAL_SHOP.getName())) {
            shop = Shop.GLOBAL_SHOP;
        } else {
            shop = new Shop(name, player);
        }
        
        shop.itemsForSale.clear();
        if(yml.contains("sale")) {
            for(String k : ((MemorySection) yml.get("sale")).getKeys(false)) {
                String v = yml.getString("sale." + k);
                List<DomsItem> cItems;
                try {
                    cItems = DomsItem.createItems(v);
                } catch(InvalidItemException e) {continue;}
                
                if(cItems == null || cItems.size() < 1) {continue;}
                
                BuyableItem item = new BuyableItem(shop, cItems.get(0), cItems.size());
                shop.addItemForSale(item);
            }
        }
        
        shop.itemsForSelling.clear();
        if(yml.contains("sell")) {
            for(String k : ((MemorySection) yml.get("sell")).getKeys(false)) {
                String v = yml.getString("sell." + k);
                List<DomsItem> cItems;
                try {
                    cItems = DomsItem.createItems(v);
                } catch(InvalidItemException e) {continue;}
                
                if(cItems == null || cItems.size() < 1) {continue;}
                
                SellableItem item = new SellableItem(shop, cItems.get(0), cItems.size());
                shop.addItemForSelling(item);
            }
        }
        shop.update();
        return shop;
    }
    
    private static Shop loadError(String message, String name) {
        Base.log("Error loading Shop \"" + (name == null ? "Unknown" : name) + "\", " + message);
        return null;
    }
    
    //Instance
    private final DomsInventoryGUI gui;
    private final DomsInventoryGUI buy;
    private final DomsInventoryGUI sell;
    
    private final List<ShopItem> itemsForSale;
    private final List<ShopItem> itemsForSelling;
    
    private final OfflinePlayer owner;
    
    public Shop(String name, OfflinePlayer owner) {
        name = Base.trim(name, MAX_SHOP_NAME_LENGTH);
        this.owner = owner;
        
        this.gui = new DomsInventoryGUI(SIZE.SIZE_9);
        this.gui.setName(name);
        
        this.buy = new DomsInventoryGUI(SIZE.SIZE_54);
        this.buy.setName(ChatColor.LIGHT_PURPLE + "Buy");
        
        this.sell = new DomsInventoryGUI(SIZE.SIZE_54);
        this.sell.setName(ChatColor.GOLD + "Sell");
        
        this.itemsForSale = new ArrayList<ShopItem>();
        this.itemsForSelling = new ArrayList<ShopItem>();
        
        DomsItem buyButtonIcon = new DomsItem(Material.CHEST);
        buyButtonIcon.setName(ChatColor.LIGHT_PURPLE + "Buy");
        buyButtonIcon.setLores(new ArrayList<String>());
        buyButtonIcon.addLore(ChatColor.AQUA + "Click to buy from the store.");
        ShopButton buyButton = new ShopButton(this.gui, buyButtonIcon, this){
            @Override
            public void onClick(Player player) {
                player.closeInventory();
                player.openInventory(this.getShop().getBuy().getInventory());
            }   
        };
        
        DomsItem sellButtonIcon = new DomsItem(Material.GOLD_INGOT);
        sellButtonIcon.setName(ChatColor.GOLD + "Sell");
        sellButtonIcon.setLores(new ArrayList<String>());
        sellButtonIcon.addLore(ChatColor.GREEN + "Click to sell items to the store.");
        ShopButton sellButton = new ShopButton(this.gui, sellButtonIcon, this){
            @Override
            public void onClick(Player player) {
                player.closeInventory();
                player.openInventory(this.getShop().getSell().getInventory());
            }
        };
        
        DomsItem backButtonIcon = new DomsItem(Material.WOOD_BUTTON);
        backButtonIcon.setName(ChatColor.RED + "Go Back");
        backButtonIcon.setLores(new ArrayList<String>());
        backButtonIcon.addLore(ChatColor.DARK_RED + "Click to go back.");
        ShopButton backButton = new ShopButton(this.buy, backButtonIcon, this) {
            @Override
            public void onClick(Player player) {
                player.closeInventory();
                player.openInventory(this.getShop().getGUI().getInventory());
            }
        };
        this.sell.addButton(backButton);
        
        this.register();
        this.save();
    }
    
    public String getName() {return this.gui.getName();}
    public DomsInventoryGUI getGUI() {return this.gui;}
    public DomsInventoryGUI getBuy() {return this.buy;}
    public DomsInventoryGUI getSell() {return this.sell;}
    public List<ShopItem> getItemsForSale() {return new ArrayList<ShopItem>(this.itemsForSale);}
    public List<ShopItem> getItemsForSelling() {return new ArrayList<ShopItem>(this.itemsForSelling);}

    public void addItemForSale(ShopItem item) {this.itemsForSale.add(item); this.update();}
    public void addItemForSelling(ShopItem item) {this.itemsForSelling.add(item); this.update();}
    
    public final void register() {SHOPS.add(this); Base.debug("Registered Store " + this.getName());}
    public final void deregister() {SHOPS.remove(this);}
    
    public boolean isOwner(OfflinePlayer player) {
        if(this.owner == null) return false;
        return this.owner.getName().equalsIgnoreCase(player.getName());
    }
    
    public void removeItemForSale(ShopItem item) {
        this.itemsForSale.remove(item);
        this.update();
    }
    
    public void removeItemForSelling(ShopItem item) {
        this.itemsForSelling.remove(item);
        this.update();
    }
    
    public void open(Player player) {
        player.closeInventory();
        player.openInventory(this.gui.getInventory());
    }
    
    public final void update() {
        for(ShopItem item : this.itemsForSale) {
            item.update();
        }
        for(ShopItem item : this.itemsForSelling) {
            item.update();
        }
        
        this.gui.update();
        this.buy.update();
        this.sell.update();
    }
    
    public final boolean save() {
        File folder = ShopManager.STORE_FOLDER;
        if(!folder.exists()) {
            if(!folder.mkdir()) {
                return saveError("Can't create directory.");
            }
        }
        
        File file = new File(folder, this.getName().toLowerCase() + ".yml");
        if(!file.exists()) {
            try {
                if(!file.createNewFile()) throw new IOException("");
            } catch(IOException e) {
                return saveError("Can't create shop file.");
            }
        }
        
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        if(yml == null) return saveError("Failed to create YML Configuration.");
        
        yml.set("name", this.getName());
        if(this.owner != null) yml.set("owner", this.owner.getName().toLowerCase());
        
        if(yml.contains("sale")) {
            //Clear//
            yml = DataManager.removeFromYml("sale", yml);
        }
        
        if(yml == null) return saveError ("Failed to remove old data.");
        
        if(this.itemsForSale != null && this.itemsForSale.size() > 1) {
            //Store Items that are for sale.//
            int id = 0;
            for(ShopItem item : this.itemsForSale) {
                id++;
                String key = "sale.item" + id + "";
                String s = "{size:\"" + item.getStock() + "\"}," + item.getIcon().toString();
                yml.set(key, s);
            }
        }
        
        if(yml.contains("sell")) {
            yml = DataManager.removeFromYml("sell", yml);
        }
        if(yml == null) return saveError ("Failed to remove old data.");
        
        if(this.itemsForSelling != null && this.itemsForSelling.size() > 1) {
            int id = 0;
            for(ShopItem item : this.itemsForSelling) {
                id++;
                String key = "sell.item" + id + "";
                String s = "{size:\"" + item.getStock() + "\"}," + item.getIcon().toString();
                yml.set(key, s);
            }
        }
        
        try {yml.save(file);} 
        catch (IOException e) 
        {return saveError ("Failed to save YML.");}
        return true;
    }
    
    private boolean saveError(String cause) {
        Base.log("Error saving Shop \"" + (this.getName() == null ? "Unknown" : this.getName()) + "\", " + cause);
        return false;
    }
}
