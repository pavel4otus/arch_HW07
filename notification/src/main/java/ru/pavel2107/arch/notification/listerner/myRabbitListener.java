package ru.pavel2107.arch.notification.listerner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.pavel2107.arch.notification.config.RabbitConfig;
import ru.pavel2107.arch.register.domain.UserRegistration;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import ru.pavel2107.arch.notification.service.NotificationService;

import java.util.HashMap;
import java.util.Map;

@Component
public class myRabbitListener {

    private NotificationService notificationService;

    @Autowired
    public myRabbitListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }


    @RabbitListener(queues = RabbitConfig.REGISTRATION_QUEUE)
    public void listen(UserRegistration registration) {
        System.out.println("Notification. command from registration : " + registration.getCommand());
        System.out.println("Notification. user : " + registration.getUser());
        Map<String, String> map = new HashMap<>();
        map.put("destination", "email");
        map.put("to", registration.getUser().getEmail());
        map.put("to", "paveltseitlin@mail.ru");
        switch (registration.getCommand()) {
            case "registered":
                map.put("subject", "Поздравляем с регистрацией");
                map.put("text", "Позитив " + registration.getUser().getFio() + "");
                break;
            case "updated":
                map.put("subject", "Профиль обновлен");
                map.put("text", "<HTML><body>Ваш профиль обновлен " + registration.getUser().getFio() + "</body></HTML>");
                break;
        }
        notificationService.send(map);
    }

}
