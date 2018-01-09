package de.htwdd.htwdresden.classes.API;

import java.util.List;

import de.htwdd.htwdresden.types.canteen.Canteen;
import de.htwdd.htwdresden.types.canteen.Meal;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Mensa API Service
 *
 * @author Kay Förster
 */
public interface ICanteenService {
    @GET("emeal/meals/{canteenID}")
    Call<List<Meal>> listMeals(@Path("canteenID") String canteenID);

    @GET("emeal/canteens")
    Call<List<Canteen>> listCanteens();
}
