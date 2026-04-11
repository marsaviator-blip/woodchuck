package org.hatchery.testArena;

import java.util.Map;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.stringtemplate.v4.ST;


public class PromptExample {
    public static void main(String[] args) {
        // Define the template with <placeholders>
        String template = "You are a helpful assistant. Please summarize the following text about <topic>: <content>";

        ST st = new ST(template);
        st.add("topic", "Quantum Computing");
        st.add("content", "Quantum computing is a type of computing that uses quantum-mechanical phenomena...");

        // Render the final prompt string
        String finalPrompt = st.render();
        System.out.println(finalPrompt);

        // adding an example of using a template file

        //getItinerary("Paris", "art");
    }

    @Value("classpath:/prompts/travel-agent.st")
    private static Resource itineraryTemplate;
    public static String getItinerary(String city, String interest) {
        PromptTemplate template = new PromptTemplate(itineraryTemplate);
        return template.render(Map.of("city", city, "interest", interest));
    }
}

// From project base run me like this:
// mvn exec:java -Dexec.mainClass="org.hatchery.testArena.PromptExample"