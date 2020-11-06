package com.example.deliveryRoute.controllers;

import com.example.deliveryRoute.core.Core;
import com.example.deliveryRoute.model.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    private Core core;

    @Autowired
    public Controller(Core core) {
        this.core = core;
    }

    @PostMapping("/createRoute")
    public Route getRoute(@RequestBody String body)
    {
        return core.createRouteFromJson(body);
    }

}