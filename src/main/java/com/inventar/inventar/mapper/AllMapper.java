package com.inventar.inventar.mapper;

import com.inventar.inventar.act.model.Act;
import com.inventar.inventar.act.model.ActDto;
import com.inventar.inventar.product.model.Product;
import com.inventar.inventar.product.model.ProductDto;
import com.inventar.inventar.product.model.ProductServiceDto;
import com.inventar.inventar.product.model.SearchProductDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AllMapper {

    ProductDto toProductDto(Product product);
    Product toProduct(ProductDto productDto);

    List<ProductDto> toProductDto(List<Product> products);

    ActDto toActDto(Act act);
    Act toAct(ActDto actDto);

    List<ActDto> toActDto(List<Act> acts);

    ProductServiceDto toProductServiceDto(Product product);
    Product toProduct(ProductServiceDto productServiceDto);

    List<ProductServiceDto> toProductServiceDto(List<Product> product);

    List<SearchProductDto> toSearchProductDto(List<Product> products);
    SearchProductDto toSearchProductDto(SearchProductDto searchProductDto);

}
