package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;
    private List<Product> productList;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("99.99"))
                .stockQuantity(10)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        productList = Arrays.asList(
                testProduct,
                Product.builder()
                        .id(2L)
                        .name("Another Product")
                        .description("Another Description")
                        .price(new BigDecimal("149.99"))
                        .stockQuantity(5)
                        .build()
        );
    }

    @Test
    void createProduct_Success() {
        when(productRepository.existsByName(anyString())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        Product result = productService.createProduct(testProduct);

        assertNotNull(result);
        assertEquals(testProduct.getName(), result.getName());
        assertEquals(testProduct.getPrice(), result.getPrice());
        verify(productRepository).existsByName(testProduct.getName());
        verify(productRepository).save(testProduct);
    }

    @Test
    void createProduct_DuplicateName_ThrowsException() {
        when(productRepository.existsByName(anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> 
            productService.createProduct(testProduct)
        );
        verify(productRepository).existsByName(testProduct.getName());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void getProductById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        Optional<Product> result = productService.getProductById(1L);

        assertTrue(result.isPresent());
        assertEquals(testProduct, result.get());
        verify(productRepository).findById(1L);
    }

    @Test
    void getProductById_NotFound_ReturnsEmpty() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Product> result = productService.getProductById(1L);

        assertFalse(result.isPresent());
        verify(productRepository).findById(1L);
    }

    @Test
    void getAllProducts_Success() {
        when(productRepository.findAll()).thenReturn(productList);

        List<Product> result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productRepository).findAll();
    }

    @Test
    void getProductByName_Success() {
        when(productRepository.findByName("Test Product")).thenReturn(Optional.of(testProduct));

        Optional<Product> result = productService.getProductByName("Test Product");

        assertTrue(result.isPresent());
        assertEquals(testProduct, result.get());
        verify(productRepository).findByName("Test Product");
    }

    @Test
    void getProductsByPriceLessThanEqual_Success() {
        BigDecimal maxPrice = new BigDecimal("100.00");
        when(productRepository.findByPriceLessThanEqual(maxPrice))
                .thenReturn(Arrays.asList(testProduct));

        List<Product> result = productService.getProductsByPriceLessThanEqual(maxPrice);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getPrice().compareTo(maxPrice) <= 0);
        verify(productRepository).findByPriceLessThanEqual(maxPrice);
    }

    @Test
    void getLowStockProducts_Success() {
        when(productRepository.findLowStockProducts(5)).thenReturn(Arrays.asList(productList.get(1)));

        List<Product> result = productService.getLowStockProducts(5);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getStockQuantity() <= 5);
        verify(productRepository).findLowStockProducts(5);
    }

    @Test
    void updateProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        Product updateData = Product.builder()
                .name("Updated Product")
                .price(new BigDecimal("199.99"))
                .build();

        Product result = productService.updateProduct(1L, updateData);

        assertNotNull(result);
        assertEquals(updateData.getName(), result.getName());
        assertEquals(updateData.getPrice(), result.getPrice());
        assertEquals(testProduct.getDescription(), result.getDescription());
        assertEquals(testProduct.getStockQuantity(), result.getStockQuantity());
        verify(productRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateProduct_NotFound_ThrowsException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            productService.updateProduct(1L, testProduct)
        );
        verify(productRepository).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProduct_Success() {
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        productService.deleteProduct(1L);

        verify(productRepository).existsById(1L);
        verify(productRepository).deleteById(1L);
    }

    @Test
    void deleteProduct_NotFound_ThrowsException() {
        when(productRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () ->
            productService.deleteProduct(1L)
        );
        verify(productRepository).existsById(1L);
        verify(productRepository, never()).deleteById(anyLong());
    }
} 