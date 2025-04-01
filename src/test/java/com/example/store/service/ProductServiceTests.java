package com.example.store.service;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import java.util.Currency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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

    private static Price createPriceEur(double amount) {
        return new Price(BigDecimal.valueOf(amount), Currency.getInstance("EUR"));
    }

    private static BigDecimal createPriceAmount(double amount) {
        return BigDecimal.valueOf(amount).setScale(Price.DEFAULT_SCALE, Price.DEFAULT_ROUNDING_MODE);
    }

    @Test
    void getById() {
        Price price = createPriceEur(1.49);
        when(productRepository.findById(1L))
            .thenReturn(Optional.of(new Product(1L, "One", price)));

        Product result = productService.getById(1L).orElseThrow();

        assertEquals("One", result.getName());
        assertEquals(createPriceAmount(1.49), result.getPrice().getAmount());
        assertEquals("EUR", result.getPrice().getCurrency().getCurrencyCode());
        verify(productRepository).findById(1L);
    }

    @Test
    void listAllProducts() {
        Pageable pageable = Pageable.ofSize(3);
        Page<Product> products = new PageImpl<Product>(List.of(
                new Product(1L, "One", createPriceEur(1.49)),
                new Product(2L, "Two", createPriceEur(2.49)),
                new Product(3L, "Three", createPriceEur(3.49))));
        when(productRepository.findAll(any(Pageable.class))).thenReturn(products);

        Page<Product> result = productService.list(pageable);

        assertEquals(3, result.getSize());
        assertEquals("One", result.getContent().get(0).getName());
        assertEquals("Two", result.getContent().get(1).getName());
        assertEquals("Three", result.getContent().get(2).getName());
        assertEquals(createPriceAmount(1.49), result.getContent().get(0).getPrice().getAmount());
        assertEquals(createPriceAmount(2.49), result.getContent().get(1).getPrice().getAmount());
        assertEquals(createPriceAmount(3.49), result.getContent().get(2).getPrice().getAmount());
        assertEquals("EUR", result.getContent().get(0).getPrice().getCurrency().getCurrencyCode());
        assertEquals("EUR", result.getContent().get(1).getPrice().getCurrency().getCurrencyCode());
        assertEquals("EUR", result.getContent().get(2).getPrice().getCurrency().getCurrencyCode());
        verify(productRepository).findAll(pageable);
    }

    @Test
    void listEmptySet() {
        Pageable pageable = Pageable.ofSize(3);
        when(productRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        Page<Product> result = productService.list(pageable);

        assertEquals(0, result.getSize());
        verify(productRepository).findAll(pageable);
    }

    @Test
    void search() {
        Pageable pageable = Pageable.ofSize(3);
        Page<Product> products = new PageImpl<Product>(List.of(
                new Product(1L, "One apple", createPriceEur(1.49)),
                new Product(2L, "Two apples", createPriceEur(2.49)),
                new Product(3L, "Three apples", createPriceEur(3.49))));
        when(productRepository.search("Apple", pageable)).thenReturn(products);

        Page<Product> results = productRepository.search("Apple", pageable);

        assertEquals(3, results.getSize());
    }

    @Test
    void create() {
        Product product = new Product(null, "One", createPriceEur(1.49));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.create(product);

        assertEquals("One", result.getName());
        assertEquals(createPriceAmount(1.49), result.getPrice().getAmount());
        assertEquals("EUR", result.getPrice().getCurrency().getCurrencyCode());
        verify(productRepository).save(product);
    }

    @Test
    void createFreeProduct() {
        Product product = new Product(null, "Zero", createPriceEur(0));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.create(product);

        assertEquals(createPriceAmount(0), result.getPrice().getAmount());
        assertEquals("EUR", result.getPrice().getCurrency().getCurrencyCode());
        verify(productRepository).save(product);
    }

    @Test
    void createProductWithRounding() {
        Product product = new Product(null, "Zero", createPriceEur(1.495));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.create(product);

        assertEquals(createPriceAmount(1.5), result.getPrice().getAmount());
        verify(productRepository).save(product);
    }

    @Test
    void update() {
        Product target = new Product(1L, "One", createPriceEur(1.49));
        Product update = new Product(1L, "OneChanged", new Price(createPriceAmount(1.39), Currency.getInstance("RON")));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(target));
        when(productRepository.save(any(Product.class))).thenReturn(productService.prepareUpdate(target, update));

        Product result = productService.update(1L, update).orElseThrow();

        assertEquals("OneChanged", result.getName());
        assertEquals(createPriceAmount(1.39), result.getPrice().getAmount());
        assertEquals("RON", result.getPrice().getCurrency().getCurrencyCode());
        verify(productRepository).findById(1L);
        verify(productRepository).save(target);
    }

    @Test
    void patchPrice() {
        Product target = new Product(1L, "One", createPriceEur(1.49));
        Price update = new Price(createPriceAmount(1.39), Currency.getInstance("RON"));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(target));
        when(productRepository.save(any(Product.class))).thenReturn(productService.preparePatchPrice(target, update));

        Product result = productService.patchPrice(1L, update).orElseThrow();

        assertEquals(createPriceAmount(1.39), result.getPrice().getAmount());
        assertEquals("RON", result.getPrice().getCurrency().getCurrencyCode());
        verify(productRepository).findById(1L);
        verify(productRepository).save(target);
    }

    @Test
    void patchName() {
        Product target = new Product(1L, "One", createPriceEur(1.49));
        Product update = new Product(null, "OneChanged", null);
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(target));
        when(productRepository.save(any(Product.class))).thenReturn(productService.preparePatchName(target, update));

        Product result = productService.patchName(1L, update).orElseThrow();

        assertEquals("OneChanged", result.getName());
        assertEquals(createPriceAmount(1.49), result.getPrice().getAmount());
        assertEquals("EUR", result.getPrice().getCurrency().getCurrencyCode());
        verify(productRepository).findById(1L);
        verify(productRepository).save(target);
    }

    @Test
    void deleteById() {
        productService.deleteById(1L);        
        verify(productRepository).deleteById(1L);
    }
}
