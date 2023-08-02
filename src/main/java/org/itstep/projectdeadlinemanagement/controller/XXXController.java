package org.itstep.projectdeadlinemanagement.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@RestController
@RequestMapping("/api")
public class XXXController {

    // REST API endpoint, возвращает JSON
    @GetMapping("/api/data")
    @ResponseBody
    public String getJsonData() {
        // Ваш код для получения данных и формирования объекта MyData
        return "New Task";
    }

    // HTML страница с формой и скриптом для обработки JSON
    @GetMapping("/page")
    public ModelAndView getHtmlPage() {
        return new ModelAndView("page");
    }

    // Обработка формы и отправка запроса на сервер
    @PostMapping("/sendData")
    @ResponseBody
    public String sendData(@RequestBody String data) {
        // Ваш код для обработки данных
        System.out.println("Received data: " + data);
        return "Data received successfully!";
    }
}


