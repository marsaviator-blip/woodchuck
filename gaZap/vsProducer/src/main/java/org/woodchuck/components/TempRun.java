package org.woodchuck.components;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.woodchuck.services.VSmessageSender;

@Component
public class TempRun implements CommandLineRunner {

    private final VSmessageSender messageSender;

    public TempRun(VSmessageSender messageSender) {
        this.messageSender = messageSender;
        System.out.println("TempRun constructor fired");
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("TempRun executed with arguments: " + String.join(", ", args));
        // Add any initialization logic here if needed
        messageSender.sendMessage("Hello from TempRun!"); // Example message sending
        messageSender.sendMessage("Another message from TempRun!"); // Example message sending
        messageSender.sendMessage("Is it correct?");  
        messageSender.sendMessage("Final message from TempRun!"); // Example message sending
    }       
    
}
