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

package com.domsplace.CreditShops.DataManagers;

import com.domsplace.CreditShops.Bases.DataManager;
import com.domsplace.CreditShops.Enums.ManagerType;
import com.domsplace.CreditShops.Objects.DomsItem;
import com.domsplace.CreditShops.Objects.ItemPricer;
import java.io.File;
import java.io.IOException;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * @author      Dominic
 * @since       11/10/2013
 */
public class BuyableManager extends DataManager {
    private YamlConfiguration yml;
    private File file;
    
    public BuyableManager() {
        super(ManagerType.BUYABLE);
    }
    
    public YamlConfiguration getCFG() {
        return yml;
    }
    
    @Override
    public void tryLoad() throws IOException {
        this.file = new File(getDataFolder(), "buyable.yml");
        if(!this.file.exists()) {
            file.createNewFile();
            this.yml = YamlConfiguration.loadConfiguration(getPlugin().getResource("buyable.yml"));
        } else {
            this.yml = YamlConfiguration.loadConfiguration(file);
        }
        
        /*** GENERATE DEFAULT CONFIG ***/
        for(Material m : Material.values()) {
            DomsItem item = new DomsItem(m);
            if(yml.contains(item.toString())) continue;
            yml.set(item.toString(), true);
        }
        
        /*** LOAD DATA BACK IN ***/
        ItemPricer.BUYABLE.clear();
        for(String item : yml.getKeys(false)) {
            try {
                DomsItem gItem = DomsItem.createItem(item);
                boolean t = yml.getBoolean(item);
                if(!t) continue;
                ItemPricer.BUYABLE.add(gItem);
            } catch(Exception e) {}
        }
        
        //Save Data
        this.trySave();
    }
    
    @Override
    public void trySave() throws IOException {
        this.yml.save(file);
    }
}
