package org.hatchery.testArena;

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
    }
} 

// From project basde run me like this:
// mvn exec:java -Dexec.mainClass="org.hatchery.testArena.PromptExample"