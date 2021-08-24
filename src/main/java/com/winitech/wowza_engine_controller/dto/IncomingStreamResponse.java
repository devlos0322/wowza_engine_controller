package com.winitech.wowza_engine_controller.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class IncomingStreamResponse {
    private Boolean success;
    private String message;
}
