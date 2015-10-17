package de.htwdd.htwdresden.types;

/**
 * Die Klasse kapselt alle Eigenschaften für ein Item im NavigationDrawer
 */
public class NavigationDrawerItem {
    private String itemName;
    private int imgResID;

    public NavigationDrawerItem(final String itemName, final int imgResID) {
        this.itemName = itemName;
        this.imgResID = imgResID;
    }

    public NavigationDrawerItem(String itemName) {
        this.itemName = itemName;
    }

    public int getImgResID() {
        return imgResID;
    }

    public String getItemName() {
        return itemName;
    }
}
