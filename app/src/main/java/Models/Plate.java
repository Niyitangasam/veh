package Models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Sam on 1/31/2017.
 */
@IgnoreExtraProperties
public class Plate {
    public String plateId;
    public String email;

    public Plate() {
    }

    public Plate(String plateId, String email) {
        this.plateId = plateId;
        this.email = email;
    }

    public String getPlateId() {
        return plateId;
    }

    public void setPlateId(String plateId) {
        this.plateId = plateId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
