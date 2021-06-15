package com.greenlearner.product.controller;

import com.greenlearner.product.dto.Product;
import com.greenlearner.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductRepository productRepository;

    @Test
    void productById() throws Exception {
        Product product = new Product();
        product.setName("Mi TV");
        product.setPrice(10000.0);
        product.setDiscount(10);
        product.setCurrency("INR");

        when(productRepository.findById(anyString())).thenReturn(Optional.of(product));

        mockMvc.perform(get("/v1/product/some-random-id"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":null,\"name\":\"Mi TV\",\"category\":null,\"price\":10000.0,\"currency\":\"INR\",\"discount\":10.0,\"discountDescription\":null,\"imageURLs\":null}"));
    }

    @Test
    void deleteProductById() throws Exception {
        Product product = new Product();
        product.setName("Mi TV");
        product.setPrice(10000.0);
        product.setDiscount(10);
        product.setCurrency("INR");

        when(productRepository.findById(anyString())).thenReturn(Optional.of(product));

        mockMvc.perform(delete("/v1/product/some-random-id"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"SUCCESS\",\"message\":\"Product Deleted\"}"));
    }

    @Test
    void addProduct() throws Exception {
        Product product = new Product();
        product.setName("Mi TV");
        product.setPrice(10000.0);
        product.setDiscount(10);
        product.setCurrency("INR");

        when(productRepository.save(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/v1/addProduct").content("{\n" +
                "  \"name\" : \"Inner enginnering\",\n" +
                "   \"category\": {\n" +
                "        \"id\": 321,\n" +
                "        \"name\": \"yoga\",\n" +
                "        \"brand\": \"Isha Foundation\"\n" +
                "    },\n" +
                "    \"price\": 100,\n" +
                "    \"currency\": \"INR\",\n" +
                "    \"discount\": 10,\n" +
                "    \"discountDescription\": \" Year end sale offer\"\n" +
                "}").contentType("application/json")).andExpect(status().isCreated())
                .andExpect(content().json("{\"status\":\"success\",\"message\":\"Mi TVadded into the system\"}"));
    }

    @Test
    void addProduct_validation() throws Exception {
        Product product = new Product();
        product.setName("Mi TV");
        product.setPrice(10000.0);
        product.setDiscount(10);
        product.setCurrency("INR");

        when(productRepository.save(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/v1/addProduct")
                .content("{\n" +
//                "  \"name\" : \"Inner enginnering\",\n" +
                        "   \"category\": {\n" +
                        "        \"id\": 321,\n" +
                        "        \"name\": \"yoga\",\n" +
                        "        \"brand\": \"Isha Foundation\"\n" +
                        "    },\n" +
                        "    \"price\": 100,\n" +
                        "    \"currency\": \"INR\",\n" +
                        "    \"discount\": 10,\n" +
                        "    \"discountDescription\": \" Year end sale offer\"\n" +
                        "}")
                .contentType("application/json")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]", is("name : Product name should not be null")));
    }

    @Test
    void updateProduct() throws Exception {
        Product product = new Product();
        product.setId("some-random-id");
        product.setName("Mi TV");
        product.setPrice(10000.0);
        product.setDiscount(10);
        product.setCurrency("INR");

        when(productRepository.findById(anyString())).thenReturn(Optional.of(product));

        Product newProduct = new Product();
        newProduct.setId(product.getId());
        newProduct.setName("Mi TV New");

        when(productRepository.save(any(Product.class))).thenReturn(newProduct);

        mockMvc.perform(put("/v1/productUpdate")
                .content("{\n" +
                        "        \"id\": \"some-random-id\",\n" +
                        "  \"name\" : \"Inner enginnering\",\n" +
                        "   \"category\": {\n" +
                        "        \"id\": 321,\n" +
                        "        \"name\": \"yoga\",\n" +
                        "        \"brand\": \"Isha Foundation\"\n" +
                        "    },\n" +
                        "    \"price\": 100,\n" +
                        "    \"currency\": \"INR\",\n" +
                        "    \"discount\": 10,\n" +
                        "    \"discountDescription\": \" Year end sale offer\"\n" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"SUCCESS\",\"message\":\"Product Updated - Mi TV New\"}"));
    }

    @Test
    void listProducts() throws Exception {
        Product p1 = new Product();
        p1.setId("123");
        p1.setName("Mi TV New");

        Product p2 = new Product();
        p2.setId("321");
        p2.setName("Nexon");

        when(productRepository.findAll()).thenReturn(List.of(p1, p2));
        mockMvc.perform(get("/v1/productList")).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Mi TV New")))
                .andExpect(jsonPath("$[1].name", is("Nexon")));

    }
}