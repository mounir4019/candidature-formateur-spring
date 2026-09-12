package com.candidatureformateur.enums;

public enum CategorieFormateur {

    FONCTIONNAIRE("موظف"),
    UNIVERSITAIRE("جامعي"),
    AUTRE("أصناف أخرى");

    private final String label;

    CategorieFormateur(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}