package com.winitech.wowza_engine_controller.dto;

import lombok.*;

/**
 * Incoming stream response dto class
 *
 * @date 2021.08.24
 * @author Junhee Park
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class IncomingStreamResponse {
    private Boolean success;
    private String message;
}
