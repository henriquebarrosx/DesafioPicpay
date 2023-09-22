package com.picpay.bankapi.web.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.Date;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailDTO {
    private String email;
    private String subject;
    private String content;
}
