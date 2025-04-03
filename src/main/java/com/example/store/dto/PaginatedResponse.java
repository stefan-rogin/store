package com.example.store.dto;

import java.util.List;
import org.springframework.data.domain.Page;

import com.example.store.model.Product;

/**
 * DTO to ensure the consistency of the response not ensured by Page.
 * @see https://docs.spring.io/spring-data/commons/reference/repositories/core-extensions.html#core.web.pageables
 */
public class PaginatedResponse<T> {

    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;

    // Map page to PaginatedResponse
    public static PaginatedResponse<Product> of(Page<Product> products) {

        PaginatedResponse<Product> response = new PaginatedResponse<>();
        response.setContent(products.getContent());
        response.setPageNumber(products.getNumber());
        response.setPageSize(products.getSize());
        response.setTotalElements(products.getTotalElements());
        response.setTotalPages(products.getTotalPages());

        return response;
    }

    // Getters and setters
    public List<T> getContent() {
        return content;
    }
    public void setContent(List<T> content) {
        this.content = content;
    }
    public int getPageNumber() {
        return pageNumber;
    }
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
    public int getPageSize() {
        return pageSize;
    }
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    public long getTotalElements() {
        return totalElements;
    }
    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
    public int getTotalPages() {
        return totalPages;
    }
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
