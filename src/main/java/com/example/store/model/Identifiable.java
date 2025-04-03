package com.example.store.model;

import java.io.Serializable;

/**
 * Ensures getId(), as the only equality criteria used will be the Id
 */
public interface Identifiable<T extends Serializable> {
    
    T getId();
}
