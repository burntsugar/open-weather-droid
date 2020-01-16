
package com.rach.archexemplar.weatherclasses;

//import javax.annotation.Generated;
import com.google.gson.annotations.Expose;

//@Generated("org.jsonschema2pojo")
public class Coord {

    @Expose
    private Double lon;
    @Expose
    private Double lat;

    /**
     * 
     * @return
     *     The lon
     */
    public Double getLon() {
        return lon;
    }

    /**
     * 
     * @param lon
     *     The lon
     */
    public void setLon(Double lon) {
        this.lon = lon;
    }

    /**
     * 
     * @return
     *     The lat
     */
    public Double getLat() {
        return lat;
    }

    /**
     * 
     * @param lat
     *     The lat
     */
    public void setLat(Double lat) {
        this.lat = lat;
    }

}
