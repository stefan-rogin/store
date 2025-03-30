package com.example.store.service;

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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.store.model.Price;
import com.example.store.model.Product;
import com.example.store.repository.ProductRepository;

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

    private Price createPriceEur(double amount) {
        return new Price(BigDecimal.valueOf(amount), Currency.getInstance("EUR"));
    }

    @Test
    void getById() {
        Price price = createPriceEur(1.49);
        when(productRepository.findById(1L))
            .thenReturn(Optional.of(new Product(1L, "One", price)));

        Product result = productService.getById(1L);

        assertNotNull(result);
        assertEquals("One", result.getName());
        assertEquals(BigDecimal.valueOf(1.49), result.getPrice().getAmount());
        assertEquals("EUR", result.getPrice().getCurrency().getCurrencyCode());
        verify(productRepository).findById(1L);
    }

    @Test
    void failGetByIdMissing() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> productService.getById(1L));
    }

    @Test
    void listAllProducts() {
        List<Product> products = List.of(
                new Product(1L, "One", createPriceEur(1.49)),
                new Product(2L, "Two", createPriceEur(2.49)),
                new Product(3L, "Three", createPriceEur(3.49)));
        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.list();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("One", result.get(0).getName());
        assertEquals("Two", result.get(1).getName());
        assertEquals("Three", result.get(2).getName());
        assertEquals(BigDecimal.valueOf(1.49), result.get(0).getPrice().getAmount());
        assertEquals(BigDecimal.valueOf(2.49), result.get(1).getPrice().getAmount());
        assertEquals(BigDecimal.valueOf(3.49), result.get(2).getPrice().getAmount());
        assertEquals("EUR", result.get(0).getPrice().getCurrency().getCurrencyCode());
        assertEquals("EUR", result.get(1).getPrice().getCurrency().getCurrencyCode());
        assertEquals("EUR", result.get(2).getPrice().getCurrency().getCurrencyCode());
        verify(productRepository).findAll();
    }

    @Test
    void listEmptySet() {
        when(productRepository.findAll()).thenReturn(new ArrayList<Product>());

        List<Product> result = productService.list();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(productRepository).findAll();
    }

    @Test
    void create() {
        Product product = new Product("One", createPriceEur(1.49));
        when(productRepository.save(product)).thenReturn(product);

        Product result = productService.create(product);

        assertNotNull(result);
        assertEquals("One", result.getName());
        assertEquals(BigDecimal.valueOf(1.49), result.getPrice().getAmount());
        assertEquals("EUR", result.getPrice().getCurrency().getCurrencyCode());
        verify(productRepository).save(product);
    }

    @Test
    void updateFull() {
        Product target = new Product(1L, "One", createPriceEur(1.49));
        Product updated = new Product(1L, "OneChanged", createPriceEur(1.39));
        when(productRepository.findById(1L)).thenReturn(Optional.of(target));
        when(productRepository.save(target)).thenReturn(updated);

        Product result = productService.updateFull(1L, updated);

        assertNotNull(result);
        assertEquals("OneChanged", result.getName());
        assertEquals(BigDecimal.valueOf(1.39), result.getPrice().getAmount());
        assertEquals("EUR", result.getPrice().getCurrency().getCurrencyCode());
        verify(productRepository).findById(1L);
        verify(productRepository).save(target);
    }

    @Test
    void failUpdateFullMissing() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.updateFull(1L, new Product("One", createPriceEur(1.49))));
    }

    @Test
    void deleteById() {
        productService.deleteById(1L);        
        verify(productRepository, times(1)).deleteById(1L);
    }
}
