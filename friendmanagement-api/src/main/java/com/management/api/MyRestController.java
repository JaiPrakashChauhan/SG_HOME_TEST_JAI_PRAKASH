package com.management.api;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyRestController {
   @GetMapping(path = "/get/xml", produces = MediaType.APPLICATION_XML_VALUE)
   public String getXML() {
      return "<user><id>12</id><name>John</name></user>";
   }
}