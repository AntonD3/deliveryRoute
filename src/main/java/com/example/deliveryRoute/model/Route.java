package com.example.deliveryRoute.model;

import java.util.ArrayList;
import java.util.List;

public class Route implements Cloneable{
    private List<RouteNode> route;
    private double length;

    public Route(){
    }

    public Route(List<RouteNode> route, double length) {
        this.route = route;
        this.length = length;
    }

    public Route(Route route)
    {
        this.length = route.getLength();
        this.route = new ArrayList<>();
        this.route.addAll(route.getRoute());
    }

    public List<RouteNode> getRoute() {
        return route;
    }

    public void setRoute(List<RouteNode> route) {
        this.route = route;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    @Override
    public Route clone(){
        Route clone = new Route();
        clone.setLength(length);
        List<RouteNode> routeClone = new ArrayList<>();
        routeClone.addAll(route);
        clone.setRoute(routeClone);
        return clone;
    }
}
