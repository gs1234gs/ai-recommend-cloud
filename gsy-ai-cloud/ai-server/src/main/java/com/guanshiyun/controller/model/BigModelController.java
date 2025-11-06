package com.guanshiyun.controller.model;

import com.guanshiyun.service.model.BigModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bigModel")
@RequiredArgsConstructor
public class BigModelController {
    private final BigModelService bigModelService;
}
