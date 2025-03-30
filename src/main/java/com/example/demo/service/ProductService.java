package com.example.demo.service;

import com.example.demo.model.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    Product createProduct(Product product);
    
    Optional<Product> getProductById(Long id);
    
    List<Product> getAllProducts();
    
    Optional<Product> getProductByName(String name);
    
    List<Product> getProductsByPriceLessThanEqual(BigDecimal price);
    
    List<Product> getLowStockProducts(Integer threshold);
    
    Product updateProduct(Long id, Product product);
    
    void deleteProduct(Long id);
    
    boolean existsByName(String name);
} 