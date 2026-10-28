package com.inventar.inventar.act.controller;

import com.inventar.inventar.act.model.ActDto;
import com.inventar.inventar.act.service.ActService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/act")
public class ActController {

    private final ActService actService;

    @PostMapping("/sentProduct")
    public ResponseEntity<ActDto> sentProduct(@RequestBody ActDto actDto) {

        return ResponseEntity.ok(actService.sentProduct(actDto));

    }

    @PostMapping("/receiveProduct")
    public ResponseEntity<ActDto> receiveProduct(@RequestBody ActDto actDto) {

        return ResponseEntity.ok(actService.receiveProduct(actDto));

    }

    @GetMapping("/getAllAct")
    public ResponseEntity<List<ActDto>> getAllActs() {

        return ResponseEntity.ok().body(actService.getAllActs());

    }

}
