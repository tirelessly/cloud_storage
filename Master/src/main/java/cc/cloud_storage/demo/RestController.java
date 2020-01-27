package cc.cloud_storage.demo;

import cc.cloud_storage.demo.master.MasterNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.Map;

@org.springframework.web.bind.annotation.RestController
public class RestController {
    private MasterNode masterNode = new MasterNode();
    private Logger log = LoggerFactory.getLogger(RestController.class);

    @RequestMapping(value = "/api/v1/insert", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<String> insert(String key, MultipartFile file) throws IOException {

        try {
            log.info("Received data to insert: {}", file.getName());
            String response = masterNode.sendInsertRequestToNode(key, file);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    @RequestMapping(value = "/api/v1/status", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<String> getStatus() throws IOException {
        return new ResponseEntity<>("Master Node is up!", HttpStatus.OK);
    }
}
