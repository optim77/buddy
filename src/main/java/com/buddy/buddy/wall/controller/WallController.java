package com.buddy.buddy.wall.controller;

import com.buddy.buddy.wall.service.WallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = "application/json")
public class WallController {

    @Autowired
    private final WallService wallService;

    public WallController(WallService wallService) {
        this.wallService = wallService;
    }

}
