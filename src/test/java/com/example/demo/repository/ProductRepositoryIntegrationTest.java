package com.example.demo.repository;

import com.example.demo.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;
    private Product anotherProduct;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

        testProduct = Product.builder()
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("99.99"))
                .stockQuantity(10)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        anotherProduct = Product.builder()
                .name("Another Product")
                .description("Another Description")
                .price(new BigDecimal("149.99"))
                .stockQuantity(5)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        productRepository.save(testProduct);
        productRepository.save(anotherProduct);
    }

    @Test
    void findByName_Success() {
        Optional<Product> result = productRepository.findByName("Test Product");

        assertTrue(result.isPresent());
        assertEquals("Test Product", result.get().getName());
        assertEquals(new BigDecimal("99.99"), result.get().getPrice());
    }

    @Test
    void findByName_NotFound_ReturnsEmpty() {
        Optional<Product> result = productRepository.findByName("Non-existent Product");

        assertFalse(result.isPresent());
    }

    @Test
    void findByPriceLessThanEqual_Success() {
        List<Product> result = productRepository.findByPriceLessThanEqual(new BigDecimal("100.00"));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getPrice().compareTo(new BigDecimal("100.00")) <= 0);
    }

    @Test
    void findLowStockProducts_Success() {
        List<Product> result = productRepository.findLowStockProducts(5);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getStockQuantity() <= 5);
    }

    @Test
    void existsByName_Success() {
        assertTrue(productRepository.existsByName("Test Product"));
        assertFalse(productRepository.existsByName("Non-existent Product"));
    }

    @Test
    void saveAndRetrieve_Success() {
        Product newProduct = Product.builder()
                .name("New Product")
                .description("New Description")
                .price(new BigDecimal("199.99"))
                .stockQuantity(15)
                .build();

        Product savedProduct = productRepository.save(newProduct);
        assertNotNull(savedProduct.getId());

        Optional<Product> retrievedProduct = productRepository.findById(savedProduct.getId());
        assertTrue(retrievedProduct.isPresent());
        assertEquals(newProduct.getName(), retrievedProduct.get().getName());
        assertEquals(newProduct.getPrice(), retrievedProduct.get().getPrice());
    }

    @Test
    void updateProduct_Success() {
        Optional<Product> productOpt = productRepository.findByName("Test Product");
        assertTrue(productOpt.isPresent());

        Product product = productOpt.get();
        product.setPrice(new BigDecimal("89.99"));
        product.setStockQuantity(20);

        Product updatedProduct = productRepository.save(product);
        assertEquals(new BigDecimal("89.99"), updatedProduct.getPrice());
        assertEquals(20, updatedProduct.getStockQuantity());
    }

    @Test
    void deleteProduct_Success() {
        Optional<Product> productOpt = productRepository.findByName("Test Product");
        assertTrue(productOpt.isPresent());

        productRepository.deleteById(productOpt.get().getId());
        assertFalse(productRepository.existsById(productOpt.get().getId()));
    }
} 