package de.htwdd.htwdresden;

class DataContainer {
    public String s_nummer;
    public String rzPasswort;
    public String abschlussValue;
    public String studienJahrgang;
    public String studiengang;
    public String studiengruppe;
    public String spinnerAbschluss;
}

public interface DataAccesser {
    DataContainer getDataContainer();
}
