package com.rutaexpress.catalog.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " no encontrado con id " + id);
    }
}
