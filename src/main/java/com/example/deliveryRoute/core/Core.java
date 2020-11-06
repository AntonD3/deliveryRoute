package com.example.deliveryRoute.core;

import com.example.deliveryRoute.model.Customer;
import com.example.deliveryRoute.model.Route;
import com.example.deliveryRoute.model.Storage;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Core {
    private DynamicProgramming dynamicProgramming;
    private GeneticAlgorithm geneticAlgorithm;
    private final int dPLimit = 20;

    @Autowired
    public Core(DynamicProgramming dynamicProgramming, GeneticAlgorithm geneticAlgorithm) {
        this.dynamicProgramming = dynamicProgramming;
        this.geneticAlgorithm = geneticAlgorithm;
    }

    private String getStartAddress(String json)
    {
        return new Gson().fromJson(json, JsonObject.class).getAsJsonPrimitive("start_address").getAsString();
    }

    private List<Storage> getStorages(String json)
    {
        Set<Map.Entry<String, JsonElement>> storagesSet = new Gson().fromJson(json, JsonObject.class).getAsJsonObject("storages").entrySet();
        Iterator<Map.Entry<String, JsonElement>> iterator = storagesSet.iterator();
        List<Storage> storages = new ArrayList<>();
        while(iterator.hasNext())
        {
            Map.Entry<String, JsonElement> entry = iterator.next();
            storages.add(new Storage(entry.getKey(), entry.getValue().getAsString()));
        }
        return storages;
    }

    private List<Customer> getCustomers(String json)
    {
        JsonArray jsonArray = new Gson().fromJson(json, JsonObject.class).getAsJsonArray("customers");
        List<Customer> customers = new ArrayList<>();
        for(int i = 0; i < jsonArray.size(); i++)
            customers.add(new Customer(jsonArray.get(i).getAsJsonObject().getAsJsonPrimitive("address").getAsString(),
                        jsonArray.get(i).getAsJsonObject().getAsJsonPrimitive("storage_id").getAsString()
                    ));
        return customers;
    }
    public Route createRouteFromJson(String json)
    {
        String startAddress = getStartAddress(json);
        List<Storage> storages = getStorages(json);
        List<Customer> customers = getCustomers(json);

        if(1 + storages.size() + customers.size() <= dPLimit)
            return dynamicProgramming.createRoute(storages, customers, startAddress);
        else
            return null;

    }
}
