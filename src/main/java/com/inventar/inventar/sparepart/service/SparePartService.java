package com.inventar.inventar.sparepart.service;

import com.inventar.inventar.enums.StockOperation;
import com.inventar.inventar.sparepart.model.RequestStock;
import com.inventar.inventar.sparepart.model.SparePart;
import com.inventar.inventar.sparepart.repository.SparePartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SparePartService {

    private final SparePartRepository sparePartRepository;

    public List<SparePart> getAllSparePart() {

        return sparePartRepository.getAllSparePart();

    }

    @Transactional
    public SparePart changeStock(RequestStock requestStock) {

        if (requestStock.getSparePartId() == null || requestStock.getAmount() == null || requestStock.getOperation() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Məlumatlar tam deyil");
        }

        if (requestStock.getAmount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Say 0-dan böyük olmalıdır");
        }

        SparePart sparePart = sparePartRepository.findByIdForUpdate(requestStock.getSparePartId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ehtiyat hissəsi tapılmadı"));

        long current = sparePart.getCount() == null ? 0L : sparePart.getCount();

        if (requestStock.getOperation() == StockOperation.PLUS) {
            sparePart.setCount(current + requestStock.getAmount());
        } else {
            if (current < requestStock.getAmount()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Stokda kifayət qədər yoxdur. Mövcud say: " + current);
            }
            sparePart.setCount(current - requestStock.getAmount());
        }

        return sparePartRepository.save(sparePart);
    }
}
