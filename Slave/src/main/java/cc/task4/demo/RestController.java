package cc.task4.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    private Logger log;

    public RestController() {
        this.log = LoggerFactory.getLogger(RestController.class);
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public ResponseEntity<String> healthcheck() {
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @RequestMapping(value = "/api/v1/insert", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<String> insert(@RequestBody Map<String, String> file) {
        Map.Entry<String,String> entry = file.entrySet().iterator().next();
        String key = entry.getKey();
        String value = entry.getValue();
        log.info("Incoming request // name: {}", key);

        return new ResponseEntity<>("HUEK",  HttpStatus.OK);
    }




}
