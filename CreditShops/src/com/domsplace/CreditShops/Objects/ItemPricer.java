package com.domsplace.CreditShops.Objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Dominic Masters
 */
public class ItemPricer {
    public static final Map<DomsItem, Double> ITEM_WORTHS = new HashMap<DomsItem, Double>();
    public static final List<DomsItem> BUYABLE = new ArrayList<DomsItem>();
    public static final List<DomsItem> SELLABLE = new ArrayList<DomsItem>();
    
    //STATIC ONLY
    public static double getPrice(DomsItem item) {
        return getPrice(ITEM_WORTHS, item);
    }
    
    public static double getPrice(Map<DomsItem, Double> worths, DomsItem item) {
        String toGuess = item.toString();
        DomsItem closestMatch = null;
        
        for(DomsItem checkItem : worths.keySet()) {
            String current = checkItem.toString();
            double cost = worths.get(checkItem);
            if(current.equalsIgnoreCase(toGuess)) return cost;
            if(!toGuess.startsWith(current)) continue;
            if(closestMatch == null) {
                closestMatch = checkItem;
                continue;
            }
            String ng = closestMatch.toString();
            closestMatch = (ng.length() > current.length() ? closestMatch : checkItem);
        }
        
        if(closestMatch == null) return 0.0d;
        return worths.get(closestMatch);
    }
    
    public static boolean isBuyable(DomsItem item) {
        String toGuess = item.toString();
        DomsItem closestMatch = null;
        
        for(DomsItem checkItem : BUYABLE) {
            String current = checkItem.toString();
            if(current.equalsIgnoreCase(toGuess)) return true;
            if(!toGuess.toLowerCase().startsWith(current.toLowerCase())) continue;
            if(closestMatch == null) {
                closestMatch = checkItem;
                continue;
            }
            String ng = closestMatch.toString();
            closestMatch = (ng.length() > current.length() ? closestMatch : checkItem);
        }
        
        return closestMatch != null;
    }
    
    public static boolean isSellable(DomsItem item) {
        String toGuess = item.toString();
        DomsItem closestMatch = null;
        
        for(DomsItem checkItem : SELLABLE) {
            String current = checkItem.toString();
            if(current.equalsIgnoreCase(toGuess)) return true;
            if(!toGuess.toLowerCase().startsWith(current.toLowerCase())) continue;
            if(closestMatch == null) {
                closestMatch = checkItem;
                continue;
            }
            String ng = closestMatch.toString();
            closestMatch = (ng.length() > current.length() ? closestMatch : checkItem);
        }
        
        return closestMatch != null;
    }
}
