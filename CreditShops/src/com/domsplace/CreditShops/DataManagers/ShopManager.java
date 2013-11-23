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
import com.domsplace.CreditShops.Objects.Shop;
import java.io.File;
import java.io.IOException;

/**
 * @author      Dominic
 * @since       11/10/2013
 */
public class ShopManager extends DataManager {
    public static File STORE_FOLDER = new File(getDataFolder(), "stores");
    
    public ShopManager() {
        super(ManagerType.SHOP);
    }
    
    @Override
    public void tryLoad() throws IOException {
        if(!STORE_FOLDER.exists()) STORE_FOLDER.mkdir();
        
        for(Shop s : Shop.getShops()) {
            if(s == null) continue;
            if(s.equals(Shop.GLOBAL_SHOP)) continue;
            s.deregister();
            s = null;
        }
        
        for(File f : STORE_FOLDER.listFiles()) {
            Shop.loadShop(f.getName());
        }
        
        //Save Data
        this.trySave();
    }
    
    @Override
    public void trySave() throws IOException {
        for(Shop s : Shop.getShops()) {
            s.save();
        }
    }
}
