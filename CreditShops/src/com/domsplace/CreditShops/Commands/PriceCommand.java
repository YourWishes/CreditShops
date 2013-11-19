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
import com.domsplace.CreditShops.Exceptions.InvalidItemException;
import com.domsplace.CreditShops.Hooks.VaultHook;
import com.domsplace.CreditShops.Objects.DomsItem;
import com.domsplace.CreditShops.Objects.ItemPricer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * @author      Dominic
 * @since       30/10/2013
 */
public class PriceCommand extends BukkitCommand {
    public PriceCommand() {
        super("price");
    }
    
    @Override
    public boolean cmd(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length < 1 && !isPlayer(sender)) {
            sendMessage(sender, ChatError + "Please enter an item name.");
            return true;
        }
        
        DomsItem item = null;
        if(args.length > 0) {
            try {
                item = DomsItem.guessItem(Base.arrayToString(args, " "));
            } catch (InvalidItemException ex) {
                sendMessage(sender, ChatError + "This is an invalid item.");
                return true;
            }
        } else {
            item = DomsItem.createItem(getPlayer(sender).getItemInHand());
            if(item == null || item.isAir()) {
                sendMessage(sender, ChatError + "This is an invalid item.");
                return true;
            }
        }
        
        double worth = ItemPricer.getPrice(item);
        
        String v = Base.twoDecimalPlaces(worth);
        try {
            v = VaultHook.VAULT_HOOK.getEconomy().format(worth);
        } catch(Exception e) {} catch(Error e) {}
        
        sendMessage(sender, "The worth of " + ChatImportant + 
                item.toHumanString() + ChatDefault + " is " + ChatImportant + 
                worth + ChatDefault + " each.");
        return true;
    }
}
