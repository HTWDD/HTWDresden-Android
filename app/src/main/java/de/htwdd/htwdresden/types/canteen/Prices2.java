package de.htwdd.htwdresden.types.canteen;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObject;

public class Prices2 implements RealmModel {

    private double students;
    private double employees;

    public double getStudents() { return students; }

    public double getEmployees() { return employees; }
}