package com.picpay.bankapi.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum AccountTypeEnum {
    COMMON("COMMON"),
    SHOPKEEPER("SHOPKEEPER");

    public final String label;
}
