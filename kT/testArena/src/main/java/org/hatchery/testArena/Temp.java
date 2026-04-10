package org.hatchery.testArena;

import org.stringtemplate.v4.*;

public class Temp {
    
    public static void main(String[] args) {
        // 1. Create a template with a placeholder <name>
        ST hello = new ST("Hello, <name>!");

        // 2. Add an attribute (the data)
        hello.add("name", "World");

        // 3. Render the output
        String result = hello.render();
        System.out.println(result); // Output: Hello, World!
    }
}

// From project base run me like this:
// mvn exec:java -Dexec.mainClass="org.hatchery.testArena.Temp""