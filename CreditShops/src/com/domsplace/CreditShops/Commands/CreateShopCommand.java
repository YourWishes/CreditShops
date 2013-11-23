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
import static com.domsplace.CreditShops.Bases.Base.ChatError;
import static com.domsplace.CreditShops.Bases.Base.ChatImportant;
import static com.domsplace.CreditShops.Bases.Base.getConfig;
import static com.domsplace.CreditShops.Bases.Base.sendMessage;
import com.domsplace.CreditShops.Bases.BukkitCommand;
import com.domsplace.CreditShops.Objects.Shop;
import com.domsplace.CreditShops.Objects.SubCommandOption;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * @author      Dominic
 * @since       30/10/2013
 */
public class CreateShopCommand extends BukkitCommand {
    public CreateShopCommand() {
        super("createshop");
        this.addSubCommandOption(new SubCommandOption("name"));
    }
    
    @Override
    public boolean cmd(CommandSender sender, Command cmd, String label, String[] args) {
        if(!isPlayer(sender)) {
            sendMessage(sender, ChatError + "Only players can do this.");
            return true;
        }
        
        if(args.length < 1) {
            sendMessage(sender, ChatError + "Please enter a store name.");
            return true;
        }
        
        Shop s = Shop.getShop(args[0]);
        if(s != null) {
            sendMessage(sender, ChatError + "A store by that name already exists.");
            return true;
        }
        
        s = Shop.getShopFromPlayer(getPlayer(sender));
        if(s != null) {
            sendMessage(sender, ChatError + "You already own a store.");
            return true;
        }
        
        String name = args[0];
        if(name.length() > Shop.MAX_SHOP_NAME_LENGTH) {
            sendMessage(sender, ChatError + "Name is too long.");
            return true;
        }
        
        if(!Shop.isNameValid(name)) {
            sendMessage(sender, ChatError + "Store name is invalid.");
            return true;
        }
                
        if(Base.useEcon()) {
            double cash = Base.getBalance(sender.getName());
            double cost = getConfig().getDouble("cost.createshop.price", 1.0d);
            if(cash < cost) {
                sendMessage(sender, ChatError + "You don't have the " + Base.formatEcon(cost) + " needed to do this.");
                return true;
            }
            
            Base.chargePlayer(sender.getName(), cost);
            sendMessage(sender, ChatImportant + "Charged " + Base.formatEcon(cost) + ".");
        }
        
        s = new Shop(name, getPlayer(sender));
        sendMessage(sender, ChatDefault + "Created shop " + ChatImportant + s.getName() + ChatDefault + ".");
        s.save();
        return true;
    }
}
