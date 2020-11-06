package com.example.deliveryRoute.services;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class GoogleDistanceMatrixClient {
    private final String apiUrl = "https://maps.googleapis.com/maps/api/distancematrix/json";
    private final RestTemplate restTemplate;

    @Value("${googleApi.key}")
    private String appKey;

    public GoogleDistanceMatrixClient() {
        restTemplate = new RestTemplate();
    }

    public double[][] getDistanceMatrix(List<String> startPoints, List<String> endPoints){
        String resultJSON = restTemplate.getForObject(
            UriComponentsBuilder
                    .fromHttpUrl(apiUrl)
                    .queryParam("key",appKey)
                    .queryParam("origins", String.join("|",startPoints))
                    .queryParam("destinations", String.join("|",endPoints))
                    .toUriString().replaceAll("%7C","|")
            ,String.class);
        System.out.println(resultJSON);

        double[][] result = new double[startPoints.size()][endPoints.size()];

        JsonArray rows = new Gson().fromJson(resultJSON, JsonObject.class).getAsJsonArray("rows");

        for(int i = 0; i < rows.size(); i++)
        {
            JsonArray elements = rows.get(i).getAsJsonObject().getAsJsonArray("elements");
            for(int j = 0; j < elements.size(); j++)
                result[i][j] = elements.get(j).getAsJsonObject().getAsJsonObject("distance").getAsJsonPrimitive("value").getAsDouble();
        }

        return result;
    }
}
