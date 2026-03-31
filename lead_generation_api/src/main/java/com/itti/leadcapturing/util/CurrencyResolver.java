package com.itti.leadcapturing.util;

import com.itti.leadcapturing.model.Location;
import com.itti.leadcapturing.model.Supplier;

/**
 * CurrencyResolver — single source of truth for transaction currency.
 *
 * RULE
 * ────
 * Same country  (buyer location country == supplier country) → buyer's location currency
 * Cross country (different countries OR either country unknown)  → USD
 *
 * Usage:
 *   String code   = CurrencyResolver.resolve(location, supplier);
 *   String symbol = CurrencyResolver.symbolFor(code);
 */
public final class CurrencyResolver {

    private CurrencyResolver() {}

    // ── ISO-4217 code → symbol ────────────────────────────────────────────────
    private static final java.util.Map<String, String> SYMBOL_MAP;
    static {
        SYMBOL_MAP = new java.util.HashMap<>();
        SYMBOL_MAP.put("INR", "₹");
        SYMBOL_MAP.put("USD", "$");
        SYMBOL_MAP.put("EUR", "€");
        SYMBOL_MAP.put("GBP", "£");
        SYMBOL_MAP.put("AED", "د.إ");
        SYMBOL_MAP.put("SGD", "S$");
        SYMBOL_MAP.put("JPY", "¥");
        SYMBOL_MAP.put("CNY", "¥");
        SYMBOL_MAP.put("CHF", "Fr");
        SYMBOL_MAP.put("CAD", "C$");
        SYMBOL_MAP.put("AUD", "A$");
        SYMBOL_MAP.put("NZD", "NZ$");
        SYMBOL_MAP.put("SAR", "ر.س");
        SYMBOL_MAP.put("QAR", "ر.ق");
        SYMBOL_MAP.put("KWD", "د.ك");
        SYMBOL_MAP.put("BHD", ".د.ب");
        SYMBOL_MAP.put("OMR", "ر.ع.");
        SYMBOL_MAP.put("MYR", "RM");
        SYMBOL_MAP.put("THB", "฿");
        SYMBOL_MAP.put("IDR", "Rp");
        SYMBOL_MAP.put("PKR", "₨");
        SYMBOL_MAP.put("BDT", "৳");
        SYMBOL_MAP.put("LKR", "₨");
        SYMBOL_MAP.put("NPR", "₨");
        SYMBOL_MAP.put("MXN", "$");
        SYMBOL_MAP.put("BRL", "R$");
        SYMBOL_MAP.put("ZAR", "R");
        SYMBOL_MAP.put("RUB", "₽");
        SYMBOL_MAP.put("KRW", "₩");
        SYMBOL_MAP.put("HKD", "HK$");
        SYMBOL_MAP.put("TWD", "NT$");
        SYMBOL_MAP.put("VND", "₫");
    }

    /**
     * Resolve the transaction currency code.
     *
     * @param buyerLocation  the buyer's Location entity (has country + currencyCode)
     * @param supplier       the Supplier entity (has country)
     * @return ISO-4217 currency code ("INR", "USD", etc.)
     */
    public static String resolve(Location buyerLocation, Supplier supplier) {
        if (buyerLocation == null) return "USD";

        String buyerCountry    = normalise(buyerLocation.getCountry());
        String supplierCountry = supplier != null ? normalise(supplier.getCountry()) : null;

        // Both countries known AND the same → use buyer's location currency
        if (buyerCountry != null && supplierCountry != null
                && buyerCountry.equals(supplierCountry)) {
            String locationCurrency = buyerLocation.getCurrencyCode();
            return (locationCurrency != null && !locationCurrency.isBlank())
                    ? locationCurrency : "INR";
        }

        // Cross-border (or either country unknown) → USD
        return "USD";
    }

    /**
     * Resolve only by country strings (useful when entity objects aren't at hand).
     *
     * @param buyerCountry      buyer's country string
     * @param buyerCurrencyCode buyer's location currencyCode
     * @param supplierCountry   supplier's country string
     */
    public static String resolve(String buyerCountry, String buyerCurrencyCode,
                                  String supplierCountry) {
        String bc = normalise(buyerCountry);
        String sc = normalise(supplierCountry);

        if (bc != null && sc != null && bc.equals(sc)) {
            return (buyerCurrencyCode != null && !buyerCurrencyCode.isBlank())
                    ? buyerCurrencyCode : "INR";
        }
        return "USD";
    }

    /**
     * Returns the currency symbol for a given ISO-4217 code.
     * Falls back to the code itself when no symbol is registered.
     */
    public static String symbolFor(String currencyCode) {
        if (currencyCode == null || currencyCode.isBlank()) return "$";
        return SYMBOL_MAP.getOrDefault(currencyCode.toUpperCase(), currencyCode);
    }

    // ── normalise country string for comparison ───────────────────────────────
    private static String normalise(String country) {
        if (country == null || country.isBlank()) return null;
        return country.trim().toUpperCase();
    }
}