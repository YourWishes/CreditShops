/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.domsplace.CreditShops.Listeners;

import com.domsplace.CreditShops.Bases.Base;
import com.domsplace.CreditShops.Bases.DomsListener;
import com.domsplace.CreditShops.Objects.Shop;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Dominic Masters
 */
public class ShopListener extends DomsListener {
    public static final String SHOP_SIGN = "[SHOP]";
    public static final String SHOP_SIGN_COMPLETE = ChatColor.DARK_BLUE + "[Shop]";
    
    @EventHandler
    public void handleSignMade(SignChangeEvent e) {
        String line = e.getLine(1);
        if(line == null || !Base.removeColors(line).equalsIgnoreCase(SHOP_SIGN)) return;
        if(!hasPermission(e.getPlayer(), "CreditShops.shopsign")) {
            sendMessage(e.getPlayer(), ChatError + "You don't have permission to make Shop Signs.");
            e.setCancelled(true);
            return;
        }
        
        line = SHOP_SIGN_COMPLETE;
        String store = e.getLine(2);
        
        if(store == null || store.replaceAll(" ","").equals("")) {
            sendMessage(e.getPlayer(), ChatError + "Please put a shope name on line 2.");
            e.setCancelled(true);
            return;
        }
        
        Shop shop = Shop.getShop(store);
        
        if(shop == null) {
            sendMessage(e.getPlayer(), ChatError + "Couldn't find a shop by that name.");
            e.setCancelled(true);
            return;
        }
        
        if(!shop.isOwner(e.getPlayer()) && !hasPermission(e.getPlayer(), "CreditShops.override")) {
            sendMessage(e.getPlayer(), ChatError + "Only the store owner can do this.");
            e.setCancelled(true);
            return;
        }
        
        if(Base.useEcon()) {
            double cash = Base.getBalance(e.getPlayer().getName());
            double cost = getConfig().getDouble("cost.createsign.price", 1.0d);
            if(cash < cost) {
                sendMessage(e.getPlayer(), ChatError + "You don't have the " + Base.formatEcon(cost) + " needed to do this.");
                e.setCancelled(true);
                return;
            }
            
            Base.chargePlayer(e.getPlayer().getName(), cost);
            sendMessage(e.getPlayer(), ChatImportant + "Charged " + Base.formatEcon(cost) + ".");
        }
        
        e.setLine(1, line);
        e.setLine(2, shop.getName());
        
        sendMessage(e.getPlayer(), "Created Sign for shop " + ChatImportant + shop.getName());
    }
    
    @EventHandler
    public void handleShopSignInteract(PlayerInteractEvent e) {
        if(e.getClickedBlock() == null) return;
        if(e.getClickedBlock().getType() == null) return;
        if(e.getClickedBlock().getState() == null) return;
        if(!(e.getClickedBlock().getState() instanceof Sign)) return;
        
        Sign sign = (Sign) e.getClickedBlock().getState();
        String linetwo = sign.getLine(1);
        if(linetwo == null || linetwo.replaceAll(" ", "").equals("")) return;
        if(!linetwo.equals(SHOP_SIGN_COMPLETE)) return;
        String store = sign.getLine(2);
        if(store == null) return;
        Shop shop = Shop.getShopExact(store);
        if(shop == null) return;
        sendMessage(e.getPlayer(), ChatDefault + "Welcome to the store " + ChatImportant + shop.getName());
        shop.open(e.getPlayer());
    }
    
    @EventHandler
    public void handleShopSignBreaking(BlockBreakEvent e) {
        if(e.getBlock()== null) return;
        if(e.getBlock().getType() == null) return;
        if(e.getBlock().getState() == null) return;
        if(!(e.getBlock().getState() instanceof Sign)) return;
        
        Sign sign = (Sign) e.getBlock().getState();
        String linetwo = sign.getLine(1);
        if(linetwo == null || linetwo.replaceAll(" ", "").equals("")) return;
        if(!linetwo.equals(SHOP_SIGN_COMPLETE)) return;
        String store = sign.getLine(2);
        if(store == null) return;
        Shop shop = Shop.getShopExact(store);
        if(shop == null) return;
        
        if(!shop.isOwner(e.getPlayer()) && !hasPermission(e.getPlayer(), "CreditShops.override")) {
            sendMessage(e.getPlayer(), ChatError + "Only the store owner can do this.");
            e.setCancelled(true);
            return;
        }
        
        if(Base.useEcon()) {
            double refund = getConfig().getDouble("cost.createsign.refundprice", 0.0d);
            if(refund > 0) {
                Base.chargePlayer(e.getPlayer().getName(), -refund);
                sendMessage(e.getPlayer(), ChatImportant + "Refunded " + Base.formatEcon(refund) + ".");
            }
        }
        
        sendMessage(e.getPlayer(), ChatImportant + "Broke Store Sign.");
    }
}
