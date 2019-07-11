package de.htwdd.htwdresden.types.canteen;

import io.realm.RealmObject;

public class Prices extends RealmObject {

    private double students;
    private double employees;

    public double getStudents() { return students; }

    public double getEmployees() { return employees; }
}
