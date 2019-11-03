package sa.aqwas.wainnakolsample.data.db.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class Restaurant implements sa.aqwas.wainnakolsample.data.model.Restaurant {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    @ColumnInfo(name = "id")
    @SerializedName("id")
    private String id;
    @ColumnInfo(name = "name")
    @SerializedName("name")
    private String name;
    @ColumnInfo(name = "category")
    @SerializedName("cat")
    private String category;
    @ColumnInfo(name = "url")
    @SerializedName("link")
    private String url;
    @ColumnInfo(name = "rating")
    @SerializedName("rating")
    private int rating;
    @ColumnInfo(name = "latitude")
    @SerializedName("lat")
    private double latitude;
    @ColumnInfo(name = "longitude")
    @SerializedName("lon")
    private double longitude;

    public Restaurant(){
        this.id = "";
        this.name = "";
        this.category = "";
        this.url = "";
        this.rating = 5;
        this.latitude = 0.0;
        this.longitude = 0.0;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
