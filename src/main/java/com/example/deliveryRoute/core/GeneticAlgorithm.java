package com.example.deliveryRoute.core;

import com.example.deliveryRoute.model.Customer;
import com.example.deliveryRoute.model.Route;
import com.example.deliveryRoute.model.RouteNode;
import com.example.deliveryRoute.model.Storage;
import com.example.deliveryRoute.services.GoogleDistanceMatrixClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class GeneticAlgorithm {
    private GoogleDistanceMatrixClient googleDistanceMatrixClient;
    private final int cycleNumber = 300;
    private final int variantNumber = 20;
    private final int mutationNumber  = 20;
    private final int INF = 1000000007;
    @Autowired
    public GeneticAlgorithm(GoogleDistanceMatrixClient googleDistanceMatrixClient) {
        this.googleDistanceMatrixClient = googleDistanceMatrixClient;
    }

    private void calculateLength(Route route,List<Storage> storages, List<Customer> customers, Map<String,Integer> storageNums, double[][] distances)
    {
        double currDist = 0;
        boolean valid = true;
        boolean was[] = new boolean[route.getRoute().size()];
        RouteNode prev = null;
        for(RouteNode routeNode:route.getRoute())
        {
            int num = routeNode.getId();
            was[num] = true;
            if(routeNode.getType().equals("customer"))
            {
                if(was[storageNums.get(customers.get(num - 1 - storages.size()).getStorageId())] == false)
                {
                    valid = false;
                    break;
                }
            }
            if(num>0)
                currDist += distances[num][prev.getId()];
        }
        if(valid) route.setLength(currDist);
        else route.setLength(INF);
    }

    private Route mutate(Route route,List<Storage> storages, List<Customer> customers, Map<String,Integer> storageNums, double[][] distances)
    {
        int ind1;
        int ind2;
        ind1 = ThreadLocalRandom.current().nextInt(1, route.getRoute().size());
        ind2 = ThreadLocalRandom.current().nextInt(1, route.getRoute().size());
        Route clone = route.clone();
        Collections.swap(clone.getRoute(), ind1 , ind2);
        calculateLength(clone,storages,customers,storageNums,distances);
        return clone;
    }

    public Route createRoute(List<Storage> storages, List<Customer> customers, String startAddress)
    {
        List<String> allNodes = new ArrayList<>();
        List<RouteNode> routeNodes = new ArrayList<>();

        allNodes.add(startAddress);
        routeNodes.add(new RouteNode(startAddress, "startAddress", 0));

        for(int i = 0; i < storages.size(); i++)
        {
            allNodes.add(storages.get(i).getAddress());
            routeNodes.add(new RouteNode(storages.get(i).getAddress(), "storage", i + 1));
        }
        for(int i = 0; i < customers.size(); i++)
        {
            allNodes.add(customers.get(i).getAddress());
            routeNodes.add(new RouteNode(customers.get(i).getAddress(), "customer", i + 1 + storages.size()));
        }

        Map<String,Integer> storageNums = new HashMap<>();
        for(int i = 0; i < storages.size(); i++)
            storageNums.put(storages.get(i).getId(), i+1);

        double[][] distances = googleDistanceMatrixClient.getDistanceMatrix(allNodes, allNodes);

        List<Route> routeList = new ArrayList<>();
        Route startRoute = new Route(routeNodes, INF);
        calculateLength(startRoute,storages,customers,storageNums,distances);

        routeList.add(startRoute);
        while(routeList.size()<variantNumber)
        {
            routeList.add(mutate(routeList.get(0),storages,customers,storageNums,distances));
        }
        for(int cycle = 0; cycle < cycleNumber; cycle++)
        {
            for(int i = 0; i < variantNumber; i++)
                for(int mutate = 0; mutate < mutationNumber; mutate++)
                    routeList.add(mutate(routeList.get(i),storages,customers,storageNums,distances));

            Collections.sort(routeList, (a, b) -> a.getLength() < b.getLength() ? -1 : a.getLength() == b.getLength() ? 0 : 1);
            while(routeList.size()>variantNumber)
                routeList.remove(routeList.size()-1);
        }
        return routeList.get(0);
    }
}
