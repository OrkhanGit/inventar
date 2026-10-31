package com.inventar.inventar.act.service;

import com.inventar.inventar.act.model.Act;
import com.inventar.inventar.act.model.ActDto;
import com.inventar.inventar.act.repository.ActRepository;
import com.inventar.inventar.enums.Condition;
import com.inventar.inventar.enums.Status;
import com.inventar.inventar.exception.ConflictException;
import com.inventar.inventar.mapper.AllMapper;
import com.inventar.inventar.product.model.Product;
import com.inventar.inventar.product.model.ProductMovementDate;
import com.inventar.inventar.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActService {

    private final ActRepository actRepository;
    private final ProductRepository productRepository;
    private final AllMapper actMapper;

    public ActDto sentProduct(ActDto actDto, String username) {
        List<String> barcodes = actDto.getBarcode();

        if (barcodes == null || barcodes.isEmpty()) {
            throw new IllegalArgumentException("Barkod siyahısı boş ola bilməz!");
        }

        List<Product> activeProducts = productRepository.findByBarcodeInAndStatus(barcodes, Status.ACTIVE);

        Map<String, Integer> indexByBarcode = new HashMap<>();
        for (int i = 0; i < barcodes.size(); i++) {
            indexByBarcode.put(barcodes.get(i), i);
        }

        boolean toServicePlace = isServicePlace(actDto.getToLocation());

        if (!activeProducts.isEmpty()) {
            for (Product product : activeProducts) {
                product.setLocation(actDto.getToLocation());
                product.setStatus(Status.INACTIVE);
                Integer idx = indexByBarcode.get(product.getBarcode());
                if (idx != null) applyState(product, actDto, idx);

                if (toServicePlace) {
                    long current = product.getServiceCount() == null ? 0L : product.getServiceCount();
                    product.setServiceCount(current + 1);
                }
            }
            productRepository.saveAll(activeProducts);
        }

        Act act = actMapper.toAct(actDto);
        act.setOwner(username);
        act.setDate(LocalDateTime.now());
        actRepository.save(act);

        return actMapper.toActDto(act);
    }

    @Transactional
    public ActDto receiveProduct(ActDto actDto, String username) {
        List<String> barcodes = actDto.getBarcode();
        if (barcodes == null || barcodes.isEmpty()) {
            throw new IllegalArgumentException("Barkod siyahısı boş ola bilməz!");
        }

        if (new HashSet<>(barcodes).size() != barcodes.size()) {
            throw new ConflictException("Aktda təkrarlanan barkod var!");
        }

        Map<String, Product> existingByBarcode = productRepository
                .findByBarcodeIn(barcodes)
                .stream()
                .collect(Collectors.toMap(Product::getBarcode, p -> p));

        List<String> names = actDto.getName();
        LocalDateTime entryDate = actDto.getDate() != null ? actDto.getDate() : LocalDateTime.now();

        List<Product> toSave = new ArrayList<>();

        for (int i = 0; i < barcodes.size(); i++) {
            String barcode = barcodes.get(i);

            Product product = existingByBarcode.get(barcode);
            if (product == null) {
                product = new Product();
                product.setBarcode(barcode);
            }

            if (names != null && i < names.size()) {
                product.setName(names.get(i));
            }

            product.setCount(1L);
            product.setStatus(Status.ACTIVE);
            product.setLocation(actDto.getToLocation());
            product.setToLocation(actDto.getLocation());
            product.setFromUser(actDto.getFromUser());
            product.setDescription(actDto.getDescription());

            applyState(product, actDto, i);

            ProductMovementDate movement = new ProductMovementDate();
            movement.setEntryDate(entryDate);
            movement.setProduct(product);
            product.getMovementsDate().add(movement);

            toSave.add(product);
        }

        productRepository.saveAll(toSave);

        Act act = actMapper.toAct(actDto);
        act.setOwner(username);
        act.setDate(LocalDateTime.now(ZoneId.of("Asia/Baku")));
        actRepository.save(act);

        return actMapper.toActDto(act);
    }

    public List<ActDto> getAllActs() {

        List<Act> all = actRepository.findAll();
        return actMapper.toActDto(all);

    }

    private void applyState(Product product, ActDto dto, int index) {
        List<Boolean> ts = dto.getToService();
        boolean toService = ts != null && index < ts.size() && Boolean.TRUE.equals(ts.get(index));
        product.setToService(toService);

        List<String> conds = dto.getCondition();
        if (conds != null && index < conds.size() && conds.get(index) != null) {
            try {
                product.setCondition(Condition.valueOf(conds.get(index).trim().toUpperCase()));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    private static final Set<String> SERVICE_PLACES = Set.of("Soliton_LTD_MMC", "Skaner_MMC");

    private boolean isServicePlace(String place) {
        if (place == null) return false;
        String p = place.trim();
        return SERVICE_PLACES.stream().anyMatch(s -> s.equalsIgnoreCase(p));
    }


}
