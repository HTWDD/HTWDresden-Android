package de.htwdd.htwdresden.types;

public class User {
    String sNummer;
    String nickName;
    String fisrtName;
    String lastName;
    String studiengang;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFisrtName() {
        return fisrtName;
    }

    public void setFisrtName(String fisrtName) {
        this.fisrtName = fisrtName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getsNummer() {
        return sNummer;
    }

    public void setsNummer(String sNummer) {
        this.sNummer = sNummer;
    }

}
