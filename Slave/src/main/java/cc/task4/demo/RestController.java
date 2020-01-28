package cc.task4.demo;

import cc.task4.demo.Controller.NodeController;
import cc.task4.demo.Exceptions.GenericException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    private Logger log;
    private NodeController controller;

    public RestController() {
        this.log = LoggerFactory.getLogger(RestController.class);
        controller = new NodeController();
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public ResponseEntity<String> healthcheck() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/api/v1/range/{from}/{to}", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<String> range(@PathVariable("from") String from,
                                 @PathVariable("to") String to) {
        return new ResponseEntity<>("ggggg", HttpStatus.OK);
    }

    @RequestMapping(value = "/api/v1/insert", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<String> insert(@RequestBody Map<String, String> file) {
        Map.Entry<String,String> entry = file.entrySet().iterator().next();
        log.info("Incoming save request // name: {}", entry.getKey());
        return controller.saveFile(file);
    }

    @RequestMapping(value = "/api/v1/delete/{fileToDelete}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<String> delete(@PathVariable("fileToDelete") String fileToDelete) {
        log.info("Incoming delete request // name: {}", fileToDelete);
        return controller.deleteFile(fileToDelete);
    }

    @RequestMapping(value = "/api/v1/search/{fileToFind}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<String> search(@PathVariable("fileToFind") String fileToFind) {
        log.info("Incoming delete request // name: {}", fileToFind);
        return controller.searchFile(fileToFind);
    }


    @ExceptionHandler({ GenericException.class })
    public @ResponseBody ResponseEntity<?> handleException(GenericException ex, HttpServletResponse response) {
        ResponseEntity<String> result = new ResponseEntity<>(ex.getErrorName() + " " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        response.setStatus(HttpServletResponse.SC_OK); // reply with 200 OK as defined in the network documentation
        return result;
    }



}
