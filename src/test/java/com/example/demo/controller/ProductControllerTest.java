package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import com.example.demo.config.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product testProduct;

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
    }

    @Test
    @WithMockUser
    void createProduct_Success() throws Exception {
        when(productService.createProduct(any(Product.class))).thenReturn(testProduct);

        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testProduct.getId()))
                .andExpect(jsonPath("$.name").value(testProduct.getName()))
                .andExpect(jsonPath("$.price").value(testProduct.getPrice().toString()));
    }

    @Test
    @WithMockUser
    void createProduct_InvalidInput_ReturnsBadRequest() throws Exception {
        Product invalidProduct = Product.builder()
                .name("")  // Invalid: empty name
                .build();

        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void getProductById_Success() throws Exception {
        when(productService.getProductById(1L)).thenReturn(Optional.of(testProduct));

        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testProduct.getId()))
                .andExpect(jsonPath("$.name").value(testProduct.getName()));
    }

    @Test
    @WithMockUser
    void getProductById_NotFound_ReturnsNotFound() throws Exception {
        when(productService.getProductById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void getAllProducts_Success() throws Exception {
        when(productService.getAllProducts()).thenReturn(Arrays.asList(testProduct));

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testProduct.getId()))
                .andExpect(jsonPath("$[0].name").value(testProduct.getName()));
    }

    @Test
    @WithMockUser
    void searchProducts_ByPrice_Success() throws Exception {
        when(productService.getProductsByPriceLessThanEqual(any(BigDecimal.class)))
                .thenReturn(Arrays.asList(testProduct));

        mockMvc.perform(get("/api/v1/products/search")
                .param("maxPrice", "100.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testProduct.getId()))
                .andExpect(jsonPath("$[0].price").value(testProduct.getPrice().toString()));
    }

    @Test
    @WithMockUser
    void searchProducts_ByLowStock_Success() throws Exception {
        when(productService.getLowStockProducts(5))
                .thenReturn(Arrays.asList(testProduct));

        mockMvc.perform(get("/api/v1/products/search")
                .param("lowStockThreshold", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testProduct.getId()))
                .andExpect(jsonPath("$[0].stockQuantity").value(testProduct.getStockQuantity()));
    }

    @Test
    @WithMockUser
    void updateProduct_Success() throws Exception {
        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(testProduct);

        mockMvc.perform(put("/api/v1/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testProduct.getId()))
                .andExpect(jsonPath("$.name").value(testProduct.getName()));
    }

    @Test
    @WithMockUser
    void deleteProduct_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void unauthorized_Access_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isUnauthorized());
    }
} 