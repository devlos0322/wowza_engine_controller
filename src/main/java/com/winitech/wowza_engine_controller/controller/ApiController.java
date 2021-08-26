package com.winitech.wowza_engine_controller.controller;


import com.winitech.wowza_engine_controller.dto.IncomingStreamCreateRequest;
import com.winitech.wowza_engine_controller.dto.StreamFileCreateRequest;
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

    @PostMapping("/applications/{applicationName}/stream_files")
    public ResponseEntity createStreamFile(@PathVariable(value = "applicationName") String applicationName, @RequestBody StreamFileCreateRequest streamFileCreateRequest) {
        return this.restService.createStreamFile(applicationName, streamFileCreateRequest.getStreamFileName(), streamFileCreateRequest.getResourceUri());
    }

    @GetMapping("/applications/{applicationName}/stream_files")
    public ResponseEntity getAllStreamFiles(@PathVariable(value = "applicationName") String applicationName) {
        return this.restService.getAllStreamFiles(applicationName);
    }

    @GetMapping("/applications/{applicationName}/stream_files/{streamFileName}")
    public ResponseEntity getStreamFile(@PathVariable(value = "applicationName") String applicationName, @PathVariable(value = "streamFileName") String streamFileName) {
        return this.restService.getStreamFile(applicationName, streamFileName);
    }

    @DeleteMapping("/applications/{applicationName}/stream_files/{streamFileName}")
    public ResponseEntity deleteStreamFile(@PathVariable(value = "applicationName") String applicationName, @PathVariable(value = "streamFileName") String streamFileName) {
        return this.restService.deleteStreamFile(applicationName, streamFileName);
    }

    @PutMapping("/applications/{applicationName}/stream_files/{streamFileName}/actions/connect")
    public ResponseEntity createIncomingStream(@PathVariable(value ="applicationName") String applicationName,
                                               @PathVariable(value="streamFileName") String streamFileName,
                                               @RequestBody IncomingStreamCreateRequest incomingStreamCreateRequest) {
        return this.restService.createIncomingStream(applicationName, streamFileName, incomingStreamCreateRequest.getMediaCasterType());
    }

    @PutMapping("/applications/{applicationName}/stream_files/{streamFileName}/actions/disconnect")
    public ResponseEntity deleteIncomingStream(@PathVariable(value ="applicationName") String applicationName,
                                               @PathVariable(value="streamFileName") String streamFileName) {
        return this.restService.deleteIncomingStream(applicationName, streamFileName);
    }
}
