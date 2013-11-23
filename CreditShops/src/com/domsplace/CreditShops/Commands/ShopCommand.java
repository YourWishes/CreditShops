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
        this.addSubCommandOption(new SubCommandOption("buy", SubCommandOption.ITEM_OPTION));
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
                s.addItemForSale(new BuyableItem(s, buy.get(0), buy.size()));
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
                s.addItemForSelling(new SellableItem(s, sell.get(0), sell.size()));
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
