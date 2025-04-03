package com.example.store.model;

import java.io.Serializable;

public interface Identifiable<T extends Serializable> {
    
    T getId();
}
