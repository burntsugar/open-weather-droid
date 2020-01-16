
package com.rach.archexemplar.forecastclasses;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

//import javax.annotation.Generated;

//@Generated("org.jsonschema2pojo")
public class ForecastRootObject {

    @Expose
    private City city;
    @Expose
    private String cod;
    @Expose
    private Double message;
    @Expose
    private Integer cnt;
    @Expose
    private java.util.List<com.rach.archexemplar.forecastclasses.List> list = new ArrayList<com.rach.archexemplar.forecastclasses.List>();

    /**
     * 
     * @return
     *     The city
     */
    public City getCity() {
        return city;
    }

    /**
     * 
     * @param city
     *     The city
     */
    public void setCity(City city) {
        this.city = city;
    }

    /**
     * 
     * @return
     *     The cod
     */
    public String getCod() {
        return cod;
    }

    /**
     * 
     * @param cod
     *     The cod
     */
    public void setCod(String cod) {
        this.cod = cod;
    }

    /**
     * 
     * @return
     *     The message
     */
    public Double getMessage() {
        return message;
    }

    /**
     * 
     * @param message
     *     The message
     */
    public void setMessage(Double message) {
        this.message = message;
    }

    /**
     * 
     * @return
     *     The cnt
     */
    public Integer getCnt() {
        return cnt;
    }

    /**
     * 
     * @param cnt
     *     The cnt
     */
    public void setCnt(Integer cnt) {
        this.cnt = cnt;
    }

    /**
     * 
     * @return
     *     The list
     */
    public java.util.List<com.rach.archexemplar.forecastclasses.List> getList() {
        return list;
    }

    /**
     * 
     * @param list
     *     The list
     */
    public void setList(java.util.List<com.rach.archexemplar.forecastclasses.List> list) {
        this.list = list;
    }

}
