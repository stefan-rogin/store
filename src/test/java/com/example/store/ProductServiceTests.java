package com.example.store;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.store.model.Product;
import com.example.store.repository.ProductRepository;
import com.example.store.service.ProductService;

@SpringBootTest
public class ProductServiceTests {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(new Product(1L, "One")));

        Product result = productService.getById(1L);

        assertNotNull(result);
        assertEquals(result.getName(), "One");
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void failGetByIdMissing() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> productService.getById(1L));
    }

    @Test
    void listAllProducts() {
        List<Product> products = List.of(
                new Product(1L, "One"),
                new Product(2L, "Two"),
                new Product(3L, "Three"));
        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.list();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(result.get(0).getName(), "One");
        assertEquals(result.get(1).getName(), "Two");
        assertEquals(result.get(2).getName(), "Three");
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void listEmptySet() {
        when(productRepository.findAll()).thenReturn(new ArrayList<Product>());

        List<Product> result = productService.list();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void create() {
        Product product = new Product("One");
        when(productRepository.save(product)).thenReturn(product);

        Product result = productService.create(product);

        assertNotNull(result);
        assertEquals(result.getName(), "One");
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void updateFull() {
        Product target = new Product(1L, "One");
        Product updated = new Product(1L, "OneChanged");
        when(productRepository.findById(1L)).thenReturn(Optional.of(target));
        when(productRepository.save(target)).thenReturn(updated);

        Product result = productService.updateFull(1L, updated);

        assertNotNull(result);
        assertEquals(result.getName(), "OneChanged");
        verify(productRepository).findById(1L);
        verify(productRepository).save(target);
    }

    @Test
    void failUpdateFullMissing() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.updateFull(1L, new Product("One")));
    }

    @Test
    void deleteById() {
        productService.deleteById(1L);        
        verify(productRepository, times(1)).deleteById(1L);
    }
}
