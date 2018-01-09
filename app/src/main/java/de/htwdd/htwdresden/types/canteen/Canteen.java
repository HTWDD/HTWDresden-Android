package de.htwdd.htwdresden.types.canteen;

/**
 * Beschreibung einer Kantine
 *
 * @author Kay Förster
 */
public class Canteen {
    private int id;
    private String city;
    private String name;
    private String address;
    private Coordinates coordinates;

    public int getId() {
        return id;
    }

    public String getCity() {
        return city;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }
}
