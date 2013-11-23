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

import com.domsplace.CreditShops.Bases.BukkitCommand;
import com.domsplace.CreditShops.Bases.DataManager;
import com.domsplace.CreditShops.Objects.Shop;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * @author      Dominic
 * @since       30/10/2013
 */
public class CreditShopsCommand extends BukkitCommand {
    public CreditShopsCommand() {
        super("creditshops");
    }
    
    @Override
    public boolean cmd(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length > 0) {
            String c = args[0].toLowerCase();
            if(c.equals("reload")) {
                sendMessage(sender, "Reloading...");
                if(!DataManager.loadAll()) {
                    sendMessage(sender, ChatError + "Failed to reload! Check console for errors.");
                    return true;
                }
                sendMessage(sender, ChatImportant + "Reloaded!");
                return true;
            }
            
            if(c.equals("openstore") && isPlayer(sender)) {
                sendMessage(sender, "Opening Global Shop.");
                Shop.GLOBAL_SHOP.open(getPlayer(sender));
                return true;
            }
        }
        
        sendMessage(sender, new String[] {
            ChatColor.GREEN + " == CreditShops ==",
            ChatColor.LIGHT_PURPLE + "\tProgrammed by Dom"
        });
        return true;
    }
}
