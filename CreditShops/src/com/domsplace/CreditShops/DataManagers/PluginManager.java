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

import com.domsplace.CreditShops.Bases.Base;
import com.domsplace.CreditShops.Bases.DataManager;
import com.domsplace.CreditShops.Enums.ManagerType;
import java.io.IOException;
import java.io.InputStream;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * @author      Dominic
 * @since       11/10/2013
 */
public class PluginManager extends DataManager {
    private YamlConfiguration plugin;
    
    public PluginManager() {
        super(ManagerType.PLUGIN);
    }
    
    @Override
    public void tryLoad() throws IOException {
        if(!getDataFolder().exists()) getDataFolder().mkdir();
        InputStream is = getPlugin().getResource("plugin.yml");
        plugin = YamlConfiguration.loadConfiguration(is);
        is.close();
        
        //For the Bukkit Page
        String out = "";
        for(String key : ((MemorySection) plugin.get("commands")).getKeys(false)) {
            out += "=== " + key + " ===\n";
            try {out += "\\\\**Aliases:** " + (Base.listToString(plugin.getStringList("commands." + key + ".aliases"), ", ")) + "\n";} catch(Exception e) {}
            try {out += "\\\\**Permission:** " + plugin.getString("commands." + key + ".permission") + "\n";}catch(Exception e) {}
            try {out += "\\\\**Description:** " + plugin.getString("commands." + key + ".description") + "\n";} catch(Exception e) {}
            try {out += "\\\\**Usage:** " + plugin.getString("commands." + key + ".usage").replaceAll("\\(", "\\/\\/").replaceAll("\\)", "\\/\\/") + "\n";} catch(Exception e) {}
            out += "----\n";
        }
        
        //log("\n\n" + out);
        
        out = "";
        for(String key : ((MemorySection) plugin.get("permissions")).getKeys(true)) {
            if(key.endsWith("description") || key.endsWith("children") || !(plugin.contains("permissions." + key + ".description"))) continue;
            out += "\n----\n**Node**: //" + key + "//";
            try {out += "\n\\\\**Description**: " + plugin.getString("permissions." + key + ".description");}catch(Exception e) {}
            try {
                //out += "\n\\\\**Also Gives**: " + (Base.setToString(((MemorySection) plugin.get("permissions." + key + ".children")).getKeys(false), ", "));
            } catch(Exception e) {}
        }
        //log(out);
    }
    
    public YamlConfiguration getYML() {
        return this.plugin;
    }

    public String getVersion() {
        return plugin.getString("version");
    }

    public String getAuthor() {
        return plugin.getString("author", "Dominic");
    }
}
