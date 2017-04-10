package Models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Sam on 1/31/2017.
 */

@IgnoreExtraProperties
public class User {
    public String email;
    public String latitude;
    public String longitude;
    public User() {
    }

    public User(String email, String latitude, String longitude) {
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getEmail() {
        return email;
    }
}