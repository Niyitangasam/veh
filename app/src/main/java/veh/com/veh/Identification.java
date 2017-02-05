package veh.com.veh;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Sam on 1/28/2017.
 */
@IgnoreExtraProperties
public class Identification {
    public String email;
    public String plateId;

    public Identification() {
    }

    public Identification(String email, String plateId) {
        this.email = email;
        this.plateId = plateId;
    }

    public String getEmail() {
        return email;
    }

    public String getPlateId() {
        return plateId;
    }
}
