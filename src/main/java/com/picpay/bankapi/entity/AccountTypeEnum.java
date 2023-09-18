package com.picpay.bankapi.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum AccountTypeEnum {
    COMMON("COMMON"),
    SHOPKEEPER("SHOPKEEPER");

    public final String label;
}
