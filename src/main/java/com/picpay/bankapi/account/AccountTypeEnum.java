package com.picpay.bankapi.account;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum AccountTypeEnum {
    COMMON("COMMON"),
    SHOPKEEPER("SHOPKEEPER");

    public final String label;
}
