package com.webnet.car_meteorologia.services;

/**
 * Created by Gilmar Ocampo Nieves on 2/04/2018.
 */

public class ApiRoutes {

    //GOOGLE API
    public static String GetPlaceById = "https://maps.googleapis.com/maps/api/place/details/json";
    public static String GetRute = "https://maps.googleapis.com/maps/api/directions/json";
    public static String GetGeocodeByAddress = "https://maps.googleapis.com/maps/api/geocode/json";

    //SERVER
    public static String Login = "Observers/GetObserverByNit";
    public static String SendData = "Observers/SetValuesForma";
    public static String GetAlarmas = "Stations/GetAlertByIdTypeStacion";

}
