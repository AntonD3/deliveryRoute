package com.example.deliveryRoute.core;

import com.example.deliveryRoute.model.Customer;
import com.example.deliveryRoute.model.Route;
import com.example.deliveryRoute.model.RouteNode;
import com.example.deliveryRoute.model.Storage;
import com.example.deliveryRoute.services.GoogleDistanceMatrixClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DynamicProgramming {

    private GoogleDistanceMatrixClient googleDistanceMatrixClient;

    @Autowired
    public DynamicProgramming(GoogleDistanceMatrixClient googleDistanceMatrixClient) {
        this.googleDistanceMatrixClient = googleDistanceMatrixClient;
    }

    private boolean inSet(int mask, int v)
    {
        return (mask&(1<<v))!=0;
    }

    private int removeBit(int mask, int bitNum)
    {
        return mask^(1<<bitNum);
    }

    public Route createRoute(List<Storage> storages, List<Customer> customers, String startAddress)
    {
        List<String> allNodes = new ArrayList<>();
        for(Storage storage:storages)
            allNodes.add(storage.getAddress());
        for(Customer customer:customers)
            allNodes.add(customer.getAddress());
        allNodes.add(startAddress);

        Map<String,Integer> storageNums = new HashMap<>();
        for(int i = 0; i < storages.size(); i++)
            storageNums.put(storages.get(i).getId(), i);

        double[][] distances = googleDistanceMatrixClient.getDistanceMatrix(allNodes, allNodes);

        int routeLen = storages.size() + customers.size();
        final int INF = 1000000007;

        double[][] d = new double[1<<routeLen][routeLen];

        for(int mask = 0; mask < (1<<routeLen); mask++)
            for(int last = 0; last < routeLen; last++)
            {
                d[mask][last] = INF;
                if(mask == (1<<last))
                {
                    if(last < storages.size())
                        d[mask][last] = distances[last][routeLen];
                }
                else
                {
                    boolean valid = true;
                    if(!inSet(mask,last)) valid = false;

                    for(int i = storages.size(); i < routeLen; i++)
                    {
                        if(inSet(mask, i) && !inSet(mask,storageNums.get(customers.get(i-storages.size()).getStorageId())))
                            valid = false;
                    }
                    if(!valid) continue;
                    int nmask = removeBit(mask, last);
                    for(int i = 0; i < routeLen; i++)
                        if(d[nmask][i] + distances[i][last] < d[mask][last])
                            d[mask][last] = d[nmask][i] + distances[i][last];
                }
            }

        int last = 0;
        for(int i = 0; i < routeLen; i++)
            if(d[(1<<routeLen)-1][i] < d[(1<<routeLen)-1][last])
                last = i;
        double res = d[(1<<routeLen)-1][last];

        int mask = (1<<routeLen)-1;
        List<RouteNode> routeNodes = new ArrayList<>();
        while(true)
        {
            String type;
            if(last < storages.size()) type = "storage";
            else type = "customer";
            String address = allNodes.get(last);
            routeNodes.add(new RouteNode(address, type));

            if(mask == (1<<last))  break;

            int nmask = removeBit(mask, last);

            int nlast = 0;
            for(int i = 0; i < routeLen; i++)
                if(d[nmask][i] + distances[i][last] == d[mask][last])
                    nlast = i;
            last = nlast;
            mask = nmask;
        }
        routeNodes.add(new RouteNode(startAddress, "startAddress"));
        Collections.reverse(routeNodes);
        return new Route(routeNodes,res);
    }
}
