package de.htwdd.htwdresden.types.canteen;

import java.util.Date;
import java.util.List;

/**
 * Beschreibung einer Mahlzeit
 *
 * @author Kay Förster
 */
public class Meal {
    private float studentPrice;
    private float employeePrice;
    private String title;
    private boolean isSoldOut;
    private List<String> additives;
    private List<String> ingredients;
    private List<String> allergens;
    private Date date;
    private String detailURL;
    private String image;

    public float getStudentPrice() {
        return studentPrice;
    }

    public float getEmployeePrice() {
        return employeePrice;
    }

    public String getTitle() {
        return title;
    }

    public boolean isSoldOut() {
        return isSoldOut;
    }

    public List<String> getAdditives() {
        return additives;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public List<String> getAllergens() {
        return allergens;
    }

    public Date getDate() {
        return date;
    }

    public String getDetailURL() {
        return detailURL;
    }

    public String getImage() {
        return image;
    }
}
