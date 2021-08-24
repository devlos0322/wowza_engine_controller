package com.winitech.wowza_engine_controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Incoming stream delete request dto class
 *
 * @date 2021.08.24
 * @author Junhee Park
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class IncomingStreamDeleteRequest {
    private String applicationName;
    private String streamFileName;
}
