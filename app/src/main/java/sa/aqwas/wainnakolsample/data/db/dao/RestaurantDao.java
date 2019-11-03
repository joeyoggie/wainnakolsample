package sa.aqwas.wainnakolsample.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import sa.aqwas.wainnakolsample.data.db.entities.Restaurant;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface RestaurantDao {
    @Query("SELECT * FROM restaurant")
    LiveData<List<Restaurant>> getAll();

    @Query("SELECT * FROM restaurant WHERE id =:restaurantId LIMIT 1")
    LiveData<Restaurant> findByID(long restaurantId);

    @Query("SELECT * FROM restaurant")
    List<Restaurant> getAllItems();

    @Query("SELECT * FROM restaurant WHERE id =:restaurantId LIMIT 1")
    Restaurant getRestaurantObject(long restaurantId);

    @Insert(onConflict = REPLACE)
    void insertAll(Restaurant... restaurants);

    @Update()
    void update(Restaurant restaurant);

    @Query("delete FROM restaurant")
    void deleteAll();

    @Delete
    void delete(Restaurant restaurant);
}
