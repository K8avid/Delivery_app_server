package com.example.coligo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import org.springframework.web.reactive.function.client.WebClient;

import com.example.coligo.model.Location;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;




import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;



@Service
public class GoogleMapService {

    @Value("${google.maps.api.key}")
    private String googleApiKey;

    @Value("${google.maps.api.base-url}")
    private String baseUrl;

    private final WebClient webClient;

    @Autowired
    private RestTemplate restTemplate;

    public GoogleMapService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://maps.googleapis.com/maps/api/place/autocomplete/json").build();
    }



    public String getAutocompleteSuggestions(String input, String language) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("input", input)
                        .queryParam("key", googleApiKey)
                        .queryParam("language", language)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block(); // Blocage pour simplifier. En production, utiliser des appels asynchrones.
    }



    public double[] getCoordinates(String address) {
        try {
            WebClient webClient = WebClient.create();
            String url = String.format(
                "https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=%s",
                address.replaceAll(" ", "+"), googleApiKey);

            String response = webClient.get()
                                    .uri(url)
                                    .retrieve()
                                    .bodyToMono(String.class)
                                    .block();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response);
            if (json.get("results").size() > 0) {
                JsonNode location = json.get("results").get(0).get("geometry").get("location");
                double lat = location.get("lat").asDouble();
                double lng = location.get("lng").asDouble();
                return new double[]{lat, lng};
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Impossible de récupérer les coordonnées pour l'adresse : " + address);
    }




    public String fetchGoogleMapsScript() {
        String googleMapsUrl = "https://maps.googleapis.com/maps/api/js?key=" + googleApiKey;

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(googleMapsUrl, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                throw new RuntimeException("Erreur lors de la récupération du script Google Maps. Statut: "
                        + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur interne : " + e.getMessage(), e);
        }
    }



    public String getGeocode(double lat, double lng) {
        String url = String.format(
            "https://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f&key=%s",
            lat, lng, googleApiKey
        );
        return restTemplate.getForObject(url, String.class);
    }



    
    public Map<String, Object> getGeocode(String address) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/geocode/json")
                .queryParam("address", address)
                .queryParam("key", googleApiKey)
                .toUriString();

        return restTemplate.getForObject(url, Map.class);
    }


    public Map<String, Object> getCoordinates_2(String address) {
        RestTemplate restTemplate = new RestTemplate();
    
        try {
            String decodedAddress = URLDecoder.decode(address, StandardCharsets.UTF_8.toString());
            String url2 = String.format("%s?address=%s&key=%s", "https://maps.googleapis.com/maps/api/geocode/json", address.replace(" ", "+"), googleApiKey);
            Map<String, Object> response = restTemplate.getForObject(url2, Map.class);
            return response; // Vous pouvez traiter et retourner des données spécifiques (latitude, longitude)
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'appel à l'API Google Maps : " + e.getMessage());
        }

       
    }


    // public Location getLocationFromAddress(String address) {
    //     // Préparer l'URL de l'API Geocoding
    //     String url = String.format("%s?address=%s&key=%s", "https://maps.googleapis.com/maps/api/geocode/json", address.replace(" ", "+"), googleApiKey);

    //     // Effectuer la requête HTTP
    //     RestTemplate restTemplate = new RestTemplate();
    //     GoogleGeocodingResponse response = restTemplate.getForObject(url, GoogleGeocodingResponse.class);

    //     // Vérifier et traiter la réponse
    //     if (response != null && !response.getResults().isEmpty()) {
    //         GoogleGeocodingResponse.Result result = response.getResults().get(0);
    //         double latitude = result.getGeometry().getLocation().getLat();
    //         double longitude = result.getGeometry().getLocation().getLng();
    //         String formattedAddress = result.getFormattedAddress();

    //         // Retourner un objet Location
    //         return new Location(null, latitude, longitude, formattedAddress);
    //     } else {
    //         throw new RuntimeException("Impossible de trouver la localisation pour l'adresse : " + address);
    //     }
    // }
    
    


    public Location getLocationFromAddress(String address) {
       
        String url = String.format("%s?address=%s&key=%s", "https://maps.googleapis.com/maps/api/geocode/json", address.replace(" ", "+"), googleApiKey);

        // Effectuer la requête HTTP
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);

        try {
            // Analyser la réponse JSON
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);

            // Vérifier le statut de la réponse
            String status = root.path("status").asText();
            if (!"OK".equals(status)) {
                throw new RuntimeException("Impossible de trouver la localisation pour l'adresse : " + address);
            }

            // Extraire les données nécessaires
            JsonNode locationNode = root.path("results").get(0).path("geometry").path("location");
            double latitude = locationNode.path("lat").asDouble();
            double longitude = locationNode.path("lng").asDouble();
            String formattedAddress = root.path("results").get(0).path("formatted_address").asText();

            // Retourner un objet Location
            return new Location(null, latitude, longitude, formattedAddress);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du traitement de la réponse de Google Maps", e);
        }
    }


    public String getDirectionsByCoordinates(double originLat, double originLng, double destLat, double destLng) {
        // Formater l'URL avec les coordonnées
        // String url = String.format("%s?origin=%f,%f&destination=%f,%f&alternatives=true&key=%s", 
        //                             "https://maps.googleapis.com/maps/api/directions/json", originLat, originLng, destLat, destLng, googleApiKey);
        String url = String.format(Locale.US, "%s?origin=%f,%f&destination=%f,%f&alternatives=true&key=%s", 
                                    "https://maps.googleapis.com/maps/api/directions/json", 
                                    originLat, originLng, destLat, destLng, googleApiKey);


        RestTemplate restTemplate = new RestTemplate();
        System.out.println(url);
        return restTemplate.getForObject(url, String.class);
    }

    //===================== Plus court entre transport en commun et marche =====================================================
    public Map<String, Object> getFastestRouteTime(String originAddress, double destLat, double destLng) {
        // Convertir l'adresse de départ en coordonnées
        double[] originCoords = getCoordinates(originAddress); 
        if (originCoords == null || originCoords.length < 2) {
            throw new RuntimeException("Impossible d'obtenir les coordonnées pour l'adresse de départ");
        }

        // Construire l'URL pour le mode walking et transit (sans alternatives)
        String walkingUrl = String.format(Locale.US,
            "https://maps.googleapis.com/maps/api/directions/json?origin=%f,%f&destination=%f,%f&mode=walking&key=%s",
            originCoords[0], originCoords[1], destLat, destLng, googleApiKey);

        String transitUrl = String.format(Locale.US,
            "https://maps.googleapis.com/maps/api/directions/json?origin=%f,%f&destination=%f,%f&mode=transit&key=%s",
            originCoords[0], originCoords[1], destLat, destLng, googleApiKey);

        RestTemplate restTemplate = new RestTemplate();
        String walkingResponse = restTemplate.getForObject(walkingUrl, String.class);
        String transitResponse = restTemplate.getForObject(transitUrl, String.class);

        ObjectMapper mapper = new ObjectMapper();
        int walkingDuration = Integer.MAX_VALUE;
        int transitDuration = Integer.MAX_VALUE;

        try {
            JsonNode rootWalking = mapper.readTree(walkingResponse);
            if (rootWalking.has("routes") && rootWalking.get("routes").size() > 0) {
                JsonNode legWalking = rootWalking.get("routes").get(0).get("legs").get(0);
                walkingDuration = legWalking.get("duration").get("value").asInt();
            }
        } catch (Exception e) {
            // En cas d'erreur de parsing, walkingDuration reste Integer.MAX_VALUE
            e.printStackTrace();
        }

        try {
            JsonNode rootTransit = mapper.readTree(transitResponse);
            if (rootTransit.has("routes") && rootTransit.get("routes").size() > 0) {
                JsonNode legTransit = rootTransit.get("routes").get(0).get("legs").get(0);
                transitDuration = legTransit.get("duration").get("value").asInt();
            }
        } catch (Exception e) {
            // En cas d'erreur de parsing, transitDuration reste Integer.MAX_VALUE
            e.printStackTrace();
        }

        // Comparer les deux durées et déterminer le mode le plus rapide
        int fastestDuration;
        String fastestMode;
        if (walkingDuration <= transitDuration) {
            fastestDuration = walkingDuration;
            fastestMode = "walking";
        } else {
            fastestDuration = transitDuration;
            fastestMode = "transit";
        }

        // Retourner un résultat sous forme de Map
        Map<String, Object> result = new HashMap<>();
        result.put("duration", fastestDuration); 
        result.put("mode", fastestMode);
        return result;
    }

}
