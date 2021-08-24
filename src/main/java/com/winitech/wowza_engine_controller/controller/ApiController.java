package com.winitech.wowza_engine_controller.controller;


import com.winitech.wowza_engine_controller.dto.IncomingStreamCreateRequest;
import com.winitech.wowza_engine_controller.dto.IncomingStreamDeleteRequest;
import com.winitech.wowza_engine_controller.service.RestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final RestService restService;

    public ApiController(RestService restService) {
        this.restService = restService;
    }

    @PostMapping("/incoming_stream")
    public ResponseEntity createIncomingStream(@RequestBody IncomingStreamCreateRequest incomingStreamCreateRequest) {
        return this.restService.createIncomingStream(incomingStreamCreateRequest.getApplication_name(),
                incomingStreamCreateRequest.getStream_file_name(),
                incomingStreamCreateRequest.getMedia_caster_type());
    }


    @DeleteMapping("/incoming_stream")
    public ResponseEntity deleteIncomingStream(@RequestBody IncomingStreamDeleteRequest incomingStreamDeleteRequest) {
        return this.restService.deleteIncomingStream(incomingStreamDeleteRequest.getApplication_name(),
                incomingStreamDeleteRequest.getStream_file_name());
    }
}
