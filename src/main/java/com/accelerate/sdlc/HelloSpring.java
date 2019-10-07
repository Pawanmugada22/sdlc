package com.accelerate.sdlc;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
 
@RestController
public class HelloSpring
{
   @RequestMapping("/hello")
    public String getSampleText()
    {
      return "Hello World! First Spring boot Rest Service";
    }
}
