package com.domsplace.CreditShops.Exceptions;

public class InvalidItemException extends Exception {
    private String data;
    
    public InvalidItemException(String line) {
        super("Invalid Item Formed");
        this.data = line;
    }

    public String getItemData() {return this.data;}
}
