package io.github.yeahfo.mry.learn.common.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import static io.github.yeahfo.mry.learn.common.utils.RegexConstants.*;

@Builder
public record InvoiceTitle( @NotBlank
                            @Size( max = 100 )
                            String title,
                            @NotBlank
                            @Pattern( regexp = UNIFIED_CODE_PATTERN, message = "统一社会信用代码格式错误" )
                            String unifiedCode,
                            @NotBlank
                            @Size( max = 100 )
                            String bankName,
                            @NotBlank
                            @Pattern( regexp = BANK_ACCOUNT_PATTERN, message = "银行账号格式错误" )
                            String bankAccount,
                            @NotBlank
                            @Size( max = 100 )
                            String address,
                            @NotBlank
                            @Pattern( regexp = PHONE_PATTERN, message = "电话号码格式错误" )
                            String phone ) {
}
