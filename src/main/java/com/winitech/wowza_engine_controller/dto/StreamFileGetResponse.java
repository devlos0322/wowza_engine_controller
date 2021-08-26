package com.winitech.wowza_engine_controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Incoming stream response dto class
 *
 * @date 2021.08.26
 * @author Junhee Park
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StreamFileGetResponse {
    private Boolean success;
    private String message;
    private String streamFileName;
    private String resourceUri;
}
