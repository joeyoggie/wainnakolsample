package sa.aqwas.wainnakolsample.data.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import sa.aqwas.wainnakolsample.data.db.dao.RestaurantDao;
import sa.aqwas.wainnakolsample.data.db.entities.Restaurant;
import sa.aqwas.wainnakolsample.data.db.entities.User;

@Database(entities = {User.class, Restaurant.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract RestaurantDao restaurantDao();
}