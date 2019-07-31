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
    @GET("canteens/{canteenID}/days/{date}/meals")
    Call<List<Meal>> listMeals(@Path("canteenID") String canteenID,
                               @Path("date") String date);

    @GET("canteens?near[lat]=51.058583&near[lng]=13.738208&near[dist]=20")
    Call<List<Canteen>> listCanteensOfDD();
}