package cc.cloud_storage.demo;

import cc.cloud_storage.demo.master.MasterNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@org.springframework.web.bind.annotation.RestController
public class RestController {
    private MasterNode masterNode = new MasterNode();
    private Logger log = LoggerFactory.getLogger(RestController.class);

    @RequestMapping(value = "/api/v1/insert", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<String> insert(MultipartFile file) {

        try {
            log.info("Received data to insert: {}", file.getResource().getFilename());
            String response = masterNode.sendInsertRequestToNode(file);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/api/v1/delete/{key}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity<String> delete(@PathVariable String key) {
        log.info("Received key to delete: {}", key);
        String response = masterNode.sendDeleteRequestToNode(key);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/v1/search/{key}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<byte[]> search(@PathVariable String key) throws Exception {
        log.info("Received key to search for: {}", key);
        try {
            Map<String, byte[]> response = masterNode.sendSearchRequestToNode(key);
            Map.Entry<String, byte[]> map = response.entrySet().iterator().next();
            HttpHeaders headers = new HttpHeaders();
            headers.set("url", map.getKey());
            return new ResponseEntity<>(map.getValue(), headers, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/api/v1/range/{key1}/{key2}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<List<String>> getRange(@PathVariable String key1, @PathVariable String key2) throws Exception {
        log.info("Received key to search from: {}", key1);
        log.info("Received key to search up to: {}", key2);

        List<String> response = masterNode.sendRangeRequestToNode(key1, key2);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @RequestMapping(value = "/api/v1/status", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<String> getStatus() {
        return new ResponseEntity<>("Master Node is up!", HttpStatus.OK);
    }
}
