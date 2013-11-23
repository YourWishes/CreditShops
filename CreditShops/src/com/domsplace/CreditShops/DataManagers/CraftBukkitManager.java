/*
 * Copyright 2013 Dominic Masters.
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
import java.io.IOException;
import org.bukkit.Bukkit;

/**
 *
 * @author Dominic Masters
 */
public class CraftBukkitManager extends DataManager {
    public CraftBukkitManager() {
        super(ManagerType.CRAFT_BUKKIT);
    }
    
    @Override
    public void tryLoad() throws IOException {
        try {
            getCraftServerClass();
            debug("Found CraftBukkit at " + this.getCraftServerClassName());
        } catch(ClassNotFoundException e) {
            noCraftBukkit();
        }
    }
    
    public boolean canFindCraftBukkit() {
        try {
            return getCraftServerClass() != null;
        } catch(Exception e) {return false;}
    }
    
    public String getCraftServerClassName() throws IOException {
        Class c = Bukkit.getServer().getClass(); //returns "org.bukkit.craftbukkit.[version].CraftServer"
        if(c == null) noCraftBukkit();
        if(c.getName() == null) noCraftBukkit();
        if(!c.getSimpleName().equals("CraftServer")) noCraftBukkit();
        return c.getName();
    }
    
    public Class getCraftServerClass() throws IOException, ClassNotFoundException {
        String s = this.getCraftServerClassName();
        return Class.forName(s);
    }
    
    public Class getCraftClass(String name) {
        //Format: org.bukkit.craftbukkit.[version].[subpackages].[class]
        //Function will replace [version] with correct version (if possible)
        try {
            String n = getCraftServerClassName();
            n = n.replaceAll("CraftServer", "");
            name = n + name;
            return Class.forName(name);
        } catch(Exception e) {
            return null;
        }
    }
    
    public Class getMineClass(String name) {
        try {
            String n = getCraftServerClassName();
            n = n.replaceAll("org.bukkit.craftbukkit", "net.minecraft.server");
            n = n.replaceAll("CraftServer", "");
            name = n + name;
            return Class.forName(name);
        } catch(Exception e) {
            return null;
        }
    }
    
    public void noCraftBukkit() throws IOException {
        throw new IOException("Couldn't find CraftBukkit.");
    }
}
