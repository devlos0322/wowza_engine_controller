package com.winitech.wowza_engine_controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
public class StreamFileRsponse {
    private Boolean success;
    private String message;
}
