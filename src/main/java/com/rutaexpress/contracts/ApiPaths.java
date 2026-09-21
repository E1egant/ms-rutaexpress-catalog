package com.rutaexpress.contracts;

/**
 * Rutas canónicas de las APIs. Única fuente de verdad para que los clientes
 * (frontend y BFF) no dupliquen strings de endpoints.
 */
public final class ApiPaths {

    private ApiPaths() {
    }

    public static final String SHIPMENTS = "/api/shipments";
    public static final String CATALOG = "/api/catalog";
    public static final String SERVICES = CATALOG + "/services";
    public static final String FLEET = CATALOG + "/fleet";
    public static final String NOTIFICATIONS = "/api/notifications";
    public static final String REPORTS = "/api/reports";
    public static final String AUDIT = "/api/audit";
}
