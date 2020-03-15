package person.winteryun.fileapi.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "hello")
@RestController
public class HelloController {

    @GetMapping(value = "world")
    public JSONObject world(@RequestParam String word){
        JSONObject resp=new JSONObject();
        resp.put("key",word);
        return resp;
    }


    @PostMapping(value = "winter")
    public JSONObject wineter(@RequestBody JSONObject object){
        String word =object.getString("word");
        JSONObject resp=new JSONObject();
        resp.put("key",word);
        return resp;
    }
}
