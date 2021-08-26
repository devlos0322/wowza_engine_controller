package com.winitech.wowza_engine_controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Stream file create request dto class
 *
 * @date 2021.08.25
 * @author Junhee Park
 */

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StreamFileCreateRequest {
    private String streamFileName;
    private String resourceUri;
}
