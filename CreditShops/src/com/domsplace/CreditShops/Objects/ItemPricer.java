/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.domsplace.CreditShops.Objects;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Dominic Masters
 */
public class ItemPricer {
    public static final Map<DomsItem, Double> itemWorths = new HashMap<DomsItem, Double>();
    
    //STATIC ONLY
    public static double getPrice(DomsItem item) {
        String toGuess = item.toString();
        DomsItem closestMatch = null;
        
        for(DomsItem checkItem : itemWorths.keySet()) {
            String current = checkItem.toString();
            double cost = itemWorths.get(checkItem);
            if(current.equals(toGuess)) return cost;
            if(!toGuess.startsWith(current)) continue;
            if(closestMatch == null) {
                closestMatch = checkItem;
                continue;
            }
            String ng = closestMatch.toString();
            closestMatch = (ng.length() > current.length() ? closestMatch : checkItem);
        }
        
        if(closestMatch == null) return 0.0d;
        return itemWorths.get(closestMatch);
    }
}
