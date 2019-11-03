package sa.aqwas.wainnakolsample.data.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class User implements sa.aqwas.wainnakolsample.data.model.User {
    public static final int USER_TYPE_OWNER = 0;
    public static final int USER_TYPE_WORKER = 1;

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    @SerializedName("id")
    private long id;
    @ColumnInfo(name = "name")
    @SerializedName("name")
    private String name;
    @ColumnInfo(name = "phone_numberid")
    @SerializedName("phone_number")
    private String phoneNumber;

    public User(){
        this.id = -1;
        this.name = "";
        this.phoneNumber = "";
    }

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
