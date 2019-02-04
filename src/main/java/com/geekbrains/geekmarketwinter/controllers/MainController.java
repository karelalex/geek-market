package com.geekbrains.geekmarketwinter.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {
    // Домашнее задание:
    // - Проанализировать код и сказать что можно оптимизировать и улучшить
    // - Идеи по работе со статусами, и что делать если пользователь
    // сформировал заказ, но не оплатил, как бы нам случайно его ему не отправить?
    // * - Сделать панель управления заказами

    // Планы на будущее:
    // - Paypal
    // - Фильтры
    // - Профиль пользователя
    // - Починить пагинацию

    @RequestMapping("/")
    public String showHomePage() {
        return "index";
    }
}
