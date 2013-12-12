/*
 * Copyright 2013 Dominic.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.domsplace.CreditShops.Commands;

import com.domsplace.CreditShops.Bases.Base;
import static com.domsplace.CreditShops.Bases.Base.ChatImportant;
import static com.domsplace.CreditShops.Bases.Base.getConfig;
import static com.domsplace.CreditShops.Bases.Base.sendMessage;
import com.domsplace.CreditShops.Bases.BukkitCommand;
import com.domsplace.CreditShops.Exceptions.InvalidItemException;
import com.domsplace.CreditShops.Objects.BuyableItem;
import com.domsplace.CreditShops.Objects.DomsItem;
import com.domsplace.CreditShops.Objects.SellableItem;
import com.domsplace.CreditShops.Objects.Shop;
import com.domsplace.CreditShops.Objects.ShopItem;
import com.domsplace.CreditShops.Objects.SubCommandOption;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * @author      Dominic
 * @since       30/10/2013
 */
public class ShopCommand extends BukkitCommand {
    public ShopCommand() {
        super("shop");
        this.addSubCommandOption(SubCommandOption.SHOP_OPTION);
        this.addSubCommandOption(new SubCommandOption("sell", SubCommandOption.ITEM_OPTION));
        this.addSubCommandOption(new SubCommandOption("close"));
        this.addSubCommandOption(new SubCommandOption("buy", SubCommandOption.ITEM_OPTION));
        this.addSubCommandOption(new SubCommandOption("price", SubCommandOption.ITEM_OPTION));
    }
    
    @Override
    public boolean cmd(CommandSender sender, Command cmd, String label, String[] args) {
        if(!isPlayer(sender)) {
            sendMessage(sender, ChatError + "Only players can do this.");
            return true;
        }
        
        Shop s = Shop.GLOBAL_SHOP;
        if(args.length > 0) {
            String c = args[0];
            
            if(c.equalsIgnoreCase("sell")) {
                //Buy items in your store
                s = Shop.getShopFromPlayer(getPlayer(sender));
                
                if(s == null) {
                    sendMessage(sender, ChatError + "You don't own a shop.");
                    return true;
                }
                
                if(s.getBuy().isFull()) {
                    sendMessage(sender, ChatError + "Can't offer any more items for sale.");
                    return true;
                }
                
                List<DomsItem> buy = DomsItem.itemStackToDomsItems(getPlayer(sender).getItemInHand());
                if(args.length > 1) {
                    String x = "";
                    for(int i = 1; i < args.length; i++) {
                        x += args[i] + " ";
                    }
                    try {
                        buy = DomsItem.guessItems(x);
                    } catch(InvalidItemException e) {
                        sendMessage(sender, ChatError + "Please enter a valid item name.");
                        return true;
                    }
                }
                
                if(buy == null || buy.size() < 1 || buy.get(0).isAir()) {
                    sendMessage(sender, ChatError + "Please enter a valid item, Air is not valid.");
                    return true;
                }
                
                if(buy.size() > buy.get(0).getMaterial().getMaxStackSize()) {
                    sendMessage(sender, ChatError + "Please only enter the amount of " + buy.get(0).getMaterial().getMaxStackSize() + " or below.");
                    return true;
                }
                
                if(!DomsItem.hasItem(buy.get(0), buy.size(), getPlayer(sender).getInventory())) {
                    sendMessage(sender, ChatError + "You must have the needed items on you!");
                    return true;
                }
                
                sendMessage(sender, "Your store now has " + ChatImportant + buy.size() + " " + buy.get(0).toHumanString() + ChatDefault + " for sale.");
                DomsItem.removeItem(buy.get(0), buy.size(), getPlayer(sender).getInventory());
                BuyableItem item = new BuyableItem(s, buy.get(0), buy.size());
                s.addItemForSale(item);
                item.update();
                return true;
            }
            
            if(c.equalsIgnoreCase("buy")) {
                //Buy items in your store
                s = Shop.getShopFromPlayer(getPlayer(sender));
                
                if(s == null) {
                    sendMessage(sender, ChatError + "You don't own a shop.");
                    return true;
                }
                
                if(s.getSell().isFull()) {
                    sendMessage(sender, ChatError + "Can't offer any more items to buy.");
                    return true;
                }
                
                List<DomsItem> sell = DomsItem.itemStackToDomsItems(getPlayer(sender).getItemInHand());
                if(args.length > 1) {
                    String x = "";
                    for(int i = 1; i < args.length; i++) {
                        x += args[i] + " ";
                    }
                    try {
                        sell = DomsItem.guessItems(x);
                    } catch(InvalidItemException e) {
                        sendMessage(sender, ChatError + "Please enter a valid item name.");
                        return true;
                    }
                }
                
                if(sell == null || sell.size() < 1 || sell.get(0).isAir()) {
                    sendMessage(sender, ChatError + "Please enter a valid item, Air is not valid.");
                    return true;
                }
                
                if(sell.size() > sell.get(0).getMaterial().getMaxStackSize()) {
                    sendMessage(sender, ChatError + "Please only enter the amount of " + sell.get(0).getMaterial().getMaxStackSize() + " or below.");
                    return true;
                }
                
                sendMessage(sender, "Your store now accepts " + ChatImportant + sell.size() + " " + sell.get(0).toHumanString() + ChatDefault + " for selling.");
                SellableItem item = new SellableItem(s, sell.get(0), sell.size());
                s.addItemForSelling(item);
                item.update();
                return true;
            }
            
            if(c.equalsIgnoreCase("close")) {
                s = Shop.getShopFromPlayer(getPlayer(sender));
                if(s == null) {
                    sendMessage(sender, ChatError +  "You don't own a shop.");
                    return true;
                }
                
                for(ShopItem item : s.getItemsForSale()) {
                    DomsItem i = item.getIcon().copy();
                    i.setName(null);
                    i.setLores(new ArrayList<String>());
                    for(int x = 0; x < item.getStock(); x++) {
                        try {i.giveToPlayer(getPlayer(sender));}catch(InvalidItemException e) {}
                    }
                }
                
                
        
                if(Base.useEcon()) {
                    double refund = getConfig().getDouble("cost.closeshop.refundprice", 0.0d);
                    if(refund > 0) {
                        Base.chargePlayer(sender.getName(), -refund);
                        sendMessage(sender, ChatImportant + "Refunded " + Base.formatEcon(refund) + ".");
                    }
                }
                
                s.delete();
                s.deregister();
                s = null;
                sendMessage(sender, "Closed shop.");
                return true;
            }
            
            if(c.equalsIgnoreCase("price")) {
                if(!hasPermission(sender, "CreditShops.shopprice")) return this.noPermission(sender, cmd, label, args);
                if(args.length < 2) {
                    sendMessage(sender, ChatError + "Enter an item name or a price.");
                    return true;
                }
                
                s = Shop.getShopFromPlayer(getPlayer(sender));
                if(s == null) {
                    sendMessage(sender, ChatError +  "You don't own a shop.");
                    return true;
                }
                
                String buySellBoth = "BOTH";
                
                DomsItem item;
                double price = -1d;
                if(isDouble(args[args.length - 1])) {
                    price = getDouble(args[args.length - 1]);
                    if(args.length > 2) {
                        String x = "";
                        for(int i = 1; i < args.length - 1; i++) {
                            if(args[i].equalsIgnoreCase("BUY")) {
                                buySellBoth = "BUY";
                                continue;
                            }
                            if(args[i].equalsIgnoreCase("SELL")) {
                                buySellBoth = "SELL";
                                continue;
                            }
                            if(args[i].equalsIgnoreCase("BOTH")) {
                                buySellBoth = "BOTH";
                                continue;
                            }
                            x += args[i];
                        }
                        try {
                            item = DomsItem.guessItem(x);
                        } catch(InvalidItemException e) {
                            sendMessage(sender, ChatError + "Please enter a valid item.");
                            return true;
                        }
                    } else {
                        item = DomsItem.createItem(getPlayer(sender).getItemInHand());
                    }
                } else {
                    sendMessage(sender, ChatError + "Enter an item price.");
                    return true;
                }
                
                if(price <= 0) {
                    sendMessage(sender, ChatError + "Enter a price above 0.");
                    return true;
                }
                
                if(item == null || item.isAir()) {
                    sendMessage(sender, ChatError + "Please enter a valid item.");
                    return true;
                }
                
                boolean buy = buySellBoth.equalsIgnoreCase("BUY") || buySellBoth.equalsIgnoreCase("BOTH");
                boolean sell = buySellBoth.equalsIgnoreCase("SELL") || buySellBoth.equalsIgnoreCase("BOTH");
                
                if(buy) s.setBuyingPrice(item, price);
                if(sell) s.setSellingPrice(item, price);
                
                s.save();
                sendMessage(sender, "Set the " + (buy ? "Buying" : "Selling") + " of " + ChatImportant + 
                    item.toHumanString().replaceAll(ChatDefault, ChatImportant) + ChatDefault + " to " 
                    + ChatImportant + Base.formatEcon(price));
                return true;
            }
            
            s = Shop.getShop(c);
        }
        
        if(s == null) {
            sendMessage(sender, ChatError + "Couldn't find shop by that name.");
            return true;
        }
        
        sendMessage(sender, ChatDefault + "Welcome to the store " + ChatImportant + s.getName());
        s.open(getPlayer(sender));
        return true;
    }
}
