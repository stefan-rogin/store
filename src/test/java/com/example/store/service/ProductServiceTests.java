package com.example.store.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.math.BigDecimal;
import java.util.Currency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.store.model.Price;
import com.example.store.model.Product;
import com.example.store.repository.ProductRepository;
import com.example.store.util.ProductUpdater;

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
        Price price = new Price();
        price.setAmount(BigDecimal.valueOf(amount)
                .setScale(Price.DEFAULT_SCALE, Price.DEFAULT_ROUNDING_MODE));
        price.setCurrency(Currency.getInstance("EUR"));
        return price;
    }

    private static BigDecimal createPriceAmount(double amount) {
        return BigDecimal.valueOf(amount).setScale(Price.DEFAULT_SCALE, Price.DEFAULT_ROUNDING_MODE);
    }

    private static final UUID UUID_ONE = UUID.fromString("33b5785c-8d8a-4301-b5b3-b07b67347173");
    private static final UUID UUID_TWO = UUID.fromString("a18920fb-56cd-41c5-8264-ed617c038524");
    private static final UUID UUID_THREE = UUID.fromString("410d3bbb-67f1-479c-81b0-a852e6579eb4");
    private static final UUID UUID_FOUR = UUID.fromString("6887bf30-c00c-4897-8423-c155bbff076b");
    private static final UUID UUID_OTHER = UUID.fromString("d3b5f8a0-4c2e-4b1c-9f7d-6a2e5f8a0c2e");

    private static Product createTestProduct(String whichOne) {
        Product product = new Product();

        switch (whichOne) {
            case "One":
                product.setName("One");
                product.setId(UUID_ONE);
                product.setPrice(createPriceEur(1.49));
                break;
            case "Two":
                product.setName("Two");
                product.setId(UUID_TWO);
                product.setPrice(createPriceEur(2.49));
                break;
            case "Three":
                product.setName("Three");
                product.setId(UUID_THREE);
                product.setPrice(createPriceEur(3.49));
                break;
            default:
                product.setName(whichOne);
                product.setId(UUID_OTHER);
                product.setPrice(createPriceEur(1));
                break;
        }

        return product;
    }

    @Test
    void findById() {
        Product target = createTestProduct("One");
        when(productRepository.findById(UUID_ONE)).thenReturn(Optional.of(target));

        Product result = productService.findById(UUID_ONE).orElseThrow();

        assertEquals("One", result.getName());
        assertEquals(createPriceAmount(1.49), result.getPrice().getAmount());
        assertEquals("EUR", result.getPrice().getCurrency().getCurrencyCode());
        verify(productRepository).findById(UUID_ONE);
    }

    @Test
    void listAllProducts() {
        Pageable pageable = Pageable.ofSize(3);
        Page<Product> products = new PageImpl<Product>(List.of(
                createTestProduct("One"),
                createTestProduct("Two"),
                createTestProduct("Three")));
        when(productRepository.findAll(any(Pageable.class))).thenReturn(products);

        Page<Product> result = productService.list(pageable);

        assertEquals(3, result.getNumberOfElements());
        assertEquals("One", result.getContent().get(0).getName());
        assertEquals("Two", result.getContent().get(1).getName());
        assertEquals("Three", result.getContent().get(2).getName());
        assertEquals(UUID_ONE, result.getContent().get(0).getId());
        assertEquals(UUID_TWO, result.getContent().get(1).getId());
        assertEquals(UUID_THREE, result.getContent().get(2).getId());
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

        assertEquals(0, result.getTotalElements());
        verify(productRepository).findAll(pageable);
    }

    @Test
    void search() {
        Pageable pageable = Pageable.ofSize(3);
        Page<Product> products = new PageImpl<Product>(List.of(
                createTestProduct("One apple"),
                createTestProduct("Two apples"),
                createTestProduct("Three apples")));
        when(productRepository.search("Apple", pageable)).thenReturn(products);

        Page<Product> results = productService.search("Apple", pageable);

        assertEquals(3, results.getNumberOfElements());
        verify(productRepository).search("Apple", pageable);
    }

    @Test
    void create() {
        Product product = createTestProduct("One");
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.create(product);

        assertEquals("One", result.getName());
        assertEquals(UUID_ONE, result.getId());
        assertEquals(createPriceAmount(1.49), result.getPrice().getAmount());
        assertEquals("EUR", result.getPrice().getCurrency().getCurrencyCode());
        verify(productRepository).save(product);
    }

    @Test
    void createFreeProduct() {
        Product product = createTestProduct("Zero");
        product.setPrice(createPriceEur(0));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.create(product);

        assertEquals(createPriceAmount(0), result.getPrice().getAmount());
        assertEquals("EUR", result.getPrice().getCurrency().getCurrencyCode());
        verify(productRepository).save(product);
    }

    @Test
    void createProductWithRounding() {
        Product product = createTestProduct("One");
        product.setPrice(createPriceEur(1.495));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.create(product);

        assertEquals(createPriceAmount(1.5), result.getPrice().getAmount());
        verify(productRepository).save(product);
    }

    @Test
    void upsertForExisting() {
        Price updatedPrice = createPriceEur(1.39);
        updatedPrice.setCurrency(Currency.getInstance("RON"));

        Product target = createTestProduct("One");
        Product update = createTestProduct("OneChanged");
        update.setPrice(updatedPrice);
        when(productRepository.findById(UUID_ONE)).thenReturn(Optional.of(target));
        when(productRepository.save(any(Product.class))).thenReturn(ProductUpdater.prepareUpdate(target, update));

        Product result = productService.upsert(UUID_ONE, update);

        assertEquals("OneChanged", result.getName());
        assertEquals(UUID_ONE, result.getId());
        assertEquals(createPriceAmount(1.39), result.getPrice().getAmount());
        assertEquals("RON", result.getPrice().getCurrency().getCurrencyCode());
        verify(productRepository).findById(UUID_ONE);
        verify(productRepository).save(target);
    }

    @Test
    void upsertForNew() {
        Price price = createPriceEur(4.49);
        Product newProduct = createTestProduct("Four");
        newProduct.setPrice(price);
        when(productRepository.findById(UUID_FOUR)).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(newProduct);

        Product result = productService.upsert(UUID_FOUR, newProduct);

        assertEquals("Four", result.getName());
        assertEquals(UUID_FOUR, result.getId());
        assertEquals(createPriceAmount(4.49), result.getPrice().getAmount());
        assertEquals("EUR", result.getPrice().getCurrency().getCurrencyCode());
        verify(productRepository).findById(UUID_FOUR);
        verify(productRepository).save(newProduct);
    }

    @Test
    void patchPrice() {
        Price updatedPrice = createPriceEur(1.39);
        updatedPrice.setCurrency(Currency.getInstance("RON"));

        Product target = createTestProduct("One");
        when(productRepository.findById(UUID_ONE)).thenReturn(Optional.of(target));
        when(productRepository.save(any(Product.class)))
                .thenReturn(ProductUpdater.preparePatchPrice(target, updatedPrice));

        Product result = productService.patchPrice(UUID_ONE, updatedPrice).orElseThrow();

        assertEquals(createPriceAmount(1.39), result.getPrice().getAmount());
        assertEquals("RON", result.getPrice().getCurrency().getCurrencyCode());
        verify(productRepository).findById(UUID_ONE);
        verify(productRepository).save(target);
    }

    @Test
    void patchName() {
        Product target = createTestProduct("One");
        Product updatedHavingNullPrice = createTestProduct("OneChanged");
        updatedHavingNullPrice.setPrice(null);

        when(productRepository.findById(UUID_ONE)).thenReturn(Optional.of(target));
        when(productRepository.save(any(Product.class)))
                .thenReturn(ProductUpdater.preparePatchName(target, updatedHavingNullPrice));

        Product result = productService.patchName(UUID_ONE, updatedHavingNullPrice).orElseThrow();

        assertEquals("OneChanged", result.getName());
        assertEquals(UUID_ONE, result.getId());
        // Assert that the price is not changed
        assertEquals(createPriceAmount(1.49), result.getPrice().getAmount());
        assertEquals("EUR", result.getPrice().getCurrency().getCurrencyCode());
        verify(productRepository).findById(UUID_ONE);
        verify(productRepository).save(target);
    }

    @Test
    void deleteById() {
        productService.deleteById(UUID_ONE);
        verify(productRepository).deleteById(UUID_ONE);
    }
}
