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
import com.domsplace.CreditShops.Bases.BukkitCommand;
import com.domsplace.CreditShops.Objects.Shop;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * @author      Dominic
 * @since       30/10/2013
 */
public class ShopsCommand extends BukkitCommand {
    public ShopsCommand() {
        super("shops");
    }
    
    @Override
    public boolean cmd(CommandSender sender, Command cmd, String label, String[] args) {
        String x = "";
        List<Shop> shops = Shop.getShops();
        
        if(shops.size() < 2) {
            sendMessage(sender, ChatError + "No Shops!");
            return true;
        }
        
        List<Shop> shopsNew = new ArrayList<Shop>();
        for(Shop s : shops) {
            if(s.equals(Shop.GLOBAL_SHOP)) continue;
            shopsNew.add(s);
        }
        
        shops = shopsNew;
        
        //Sort
        Comparator<Shop> comparator = new Comparator<Shop>(){
            @Override
            public int compare(Shop o1, Shop o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
        Collections.sort(shops, comparator);
        
        for(int i = 0; i < shops.size(); i++) {
            Shop s = shops.get(i);
            if(s == null || s.getOwner() == null) continue;
            String name = s.getName();
            if(s.getOwner().isOnline() && Base.canSee(sender, s.getOwner())) {
                name = ChatColor.GREEN + name;
            } else {
                name = ChatDefault + name;
            }
            
            if(i < (shops.size() - 1)) name += ", ";
            x += name;
        }
        
        sendMessage(sender, new String[] {
            ChatImportant + "Shops: ",
            x
        });
        return true;
    }
}
