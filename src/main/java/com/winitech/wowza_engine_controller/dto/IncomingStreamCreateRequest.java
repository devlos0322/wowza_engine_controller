package com.winitech.wowza_engine_controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class IncomingStreamCreateRequest {
    private String application_name;
    private String stream_file_name;
    private String media_caster_type;
}
