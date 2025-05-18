package com.example.coligo.controller;


import com.example.coligo.service.GoogleMapService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;




@RestController
@RequestMapping("/maps")
public class GoogleMapController {

    @Autowired
    private GoogleMapService googleMapService;

    @GetMapping("/autocomplete")
    public ResponseEntity<?> autocomplete(
        @RequestParam("input") String input,
        @RequestParam(value = "language", defaultValue = "fr") String language
    ) {
        try {
            String response = googleMapService.getAutocompleteSuggestions(input, language);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de la récupération des suggestions : " + e.getMessage());
        }
    }




    @GetMapping(value = "/script", produces = "text/html")
    public ResponseEntity<String> getGoogleMapsScript() {
        try {
            String script = googleMapService.fetchGoogleMapsScript();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "text/html")
                    .body(script);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur : " + e.getMessage());
        }
    }





    @GetMapping("/geocode")
    public String getGeocode(@RequestParam double lat, @RequestParam double lng) {
        return googleMapService.getGeocode(lat, lng);
    }





    @GetMapping("/get-coordinates")
    public Map<String, Object> getGeocode(@RequestParam String address) {
        return googleMapService.getCoordinates_2(address);
    }



    @GetMapping("/directions")
    public String getDirections(
        @RequestParam String originLat,
        @RequestParam String originLng,
        @RequestParam String destLat,
        @RequestParam String destLng) {

            
        try {
            double originLatParsed = Double.parseDouble(originLat);
            double originLngParsed = Double.parseDouble(originLng);
            double destLatParsed = Double.parseDouble(destLat);
            double destLngParsed = Double.parseDouble(destLng);

            return googleMapService.getDirectionsByCoordinates(originLatParsed, originLngParsed, destLatParsed, destLngParsed);
        } catch (NumberFormatException e) {
            return "Invalid latitude or longitude format. Please ensure they are valid decimal numbers.";
        }
    }

    @GetMapping("/get-coordinates-nogeo-{address}")
    public ResponseEntity<double[]> getCoor(@PathVariable String address){
        double[] result = googleMapService.getCoordinates(address);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/fastest-route") //pour le trajet le plus court en tranport en commun ou marche
    public ResponseEntity<?> getFastestRoute(
        @RequestParam("origin") String originAddress,
        @RequestParam("destLat") double destLat,
        @RequestParam("destLng") double destLng) {
        try {
            Map<String, Object> result = googleMapService.getFastestRouteTime(originAddress, destLat, destLng);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de la récupération de l'itinéraire: " + e.getMessage());
        }
    }

    
}
