package com.winitech.wowza_engine_controller.controller;


import com.winitech.wowza_engine_controller.dto.IncomingStreamCreateRequest;
import com.winitech.wowza_engine_controller.dto.IncomingStreamDeleteRequest;
import com.winitech.wowza_engine_controller.service.RestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * API controller class
 *
 * @apiNote root : /api
 *          resource : /incoming_stream
 *          support : Create(Post), Delete
 * @date 2021.08.24
 * @author Junhee Park
 */
@RestController
@RequestMapping("/api")
public class ApiController {
    private final RestService restService;

    public ApiController(RestService restService) {
        this.restService = restService;
    }

    @PostMapping("/incoming_stream")
    public ResponseEntity createIncomingStream(@RequestBody IncomingStreamCreateRequest incomingStreamCreateRequest) {
        return this.restService.createIncomingStream(incomingStreamCreateRequest.getApplicationName(),
                incomingStreamCreateRequest.getStreamFileName(),
                incomingStreamCreateRequest.getMediaCasterType());
    }


    @DeleteMapping("/incoming_stream")
    public ResponseEntity deleteIncomingStream(@RequestBody IncomingStreamDeleteRequest incomingStreamDeleteRequest) {
        return this.restService.deleteIncomingStream(incomingStreamDeleteRequest.getApplicationName(),
                incomingStreamDeleteRequest.getStreamFileName());
    }
}
