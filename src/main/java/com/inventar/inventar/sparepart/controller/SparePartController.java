package com.inventar.inventar.sparepart.controller;

import com.inventar.inventar.sparepart.model.RequestStock;
import com.inventar.inventar.sparepart.model.SparePart;
import com.inventar.inventar.sparepart.service.SparePartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sparePart")
public class SparePartController {

    private final SparePartService sparePartService;

    @GetMapping("/getAll")
    public ResponseEntity<List<SparePart>> getAllSparePart() {
        return ResponseEntity.ok().body(sparePartService.getAllSparePart());
    }

    @PutMapping("/changeStock")
    public ResponseEntity<SparePart> changeStock(@RequestBody RequestStock requestStock) {
        return ResponseEntity.ok(sparePartService.changeStock(requestStock));
    }

    @PostMapping("/create")
    public ResponseEntity<SparePart> createSparePart(SparePart sparePart) {
        return ResponseEntity.ok().body(sparePartService.createSparePart(sparePart));
    }

}
