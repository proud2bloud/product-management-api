package com.example.demo.service.impl;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.ProductService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @CacheEvict(value = "products", allEntries = true)
    public Product createProduct(Product product) {
        if (productRepository.existsByName(product.getName())) {
            throw new IllegalArgumentException("Product with name " + product.getName() + " already exists");
        }
        return productRepository.save(product);
    }

    @Override
    @Cacheable(value = "products", key = "#id")
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    @Cacheable(value = "products")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Cacheable(value = "products", key = "'name:' + #name")
    public Optional<Product> getProductByName(String name) {
        return productRepository.findByName(name);
    }

    @Override
    @Cacheable(value = "products", key = "'price:' + #price")
    public List<Product> getProductsByPriceLessThanEqual(BigDecimal price) {
        return productRepository.findByPriceLessThanEqual(price);
    }

    @Override
    @Cacheable(value = "products", key = "'lowStock:' + #threshold")
    public List<Product> getLowStockProducts(Integer threshold) {
        return productRepository.findLowStockProducts(threshold);
    }

    @Override
    @CacheEvict(value = "products", allEntries = true)
    public Product updateProduct(Long id, Product product) {
        Product existingProduct = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Update only non-null fields
        if (product.getName() != null) {
            existingProduct.setName(product.getName());
        }
        if (product.getDescription() != null) {
            existingProduct.setDescription(product.getDescription());
        }
        if (product.getPrice() != null) {
            existingProduct.setPrice(product.getPrice());
        }
        if (product.getStockQuantity() != null) {
            existingProduct.setStockQuantity(product.getStockQuantity());
        }

        return productRepository.save(existingProduct);
    }

    @Override
    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }
} 