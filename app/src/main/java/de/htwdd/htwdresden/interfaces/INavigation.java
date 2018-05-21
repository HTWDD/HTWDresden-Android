package de.htwdd.htwdresden.interfaces;

/**
 * Schnittstellen zum setzen des Toolbar-Titels
 */
public interface INavigation {
    void setTitle(final String title);
    void goToNavigationItem(final int item);
}
