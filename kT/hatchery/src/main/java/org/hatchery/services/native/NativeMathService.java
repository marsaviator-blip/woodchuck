pacage org.hatchery.services;   

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import org.springframework.stereotype.Service;

@Service
public class NativeMathService {

    private final MethodHandle floorHandle;

    public NativeMathService() throws Throwable {
        // 1. Get the native linker for the current platform
        Linker linker = Linker.nativeLinker();
        
        // 2. Locate the function symbol (e.g., 'floor' from the standard math library)
        SymbolLookup stdlib = linker.defaultLookup();
        MemorySegment floorAddress = stdlib.find("floor")
                .orElseThrow(() -> new IllegalArgumentException("Function not found"));

        // 3. Define the descriptor (Returns a double, accepts a double)
        FunctionDescriptor descriptor = FunctionDescriptor.of(
                ValueLayout.JAVA_DOUBLE, 
                ValueLayout.JAVA_DOUBLE
        );

        // 4. Create the method handle
        this.floorHandle = linker.downcallHandle(floorAddress, descriptor);
    }

    public double callNativeFloor(double value) {
        try {
            // Invoke the native function safely
            return (double) floorHandle.invokeExact(value);
        } catch (Throwable t) {
            throw new RuntimeException("Native call failed", t);
        }
    }

    public String getNativeStringMessage() {
    // Confined arenas are fast and bounded to the execution thread
    try (Arena arena = Arena.ofConfined()) {
        // Allocate off-heap space for a C-style string
        MemorySegment nativeString = arena.allocateFrom("Hello from Spring via FFM!");
        
        // Pass 'nativeString' to your MethodHandle downcall here...
        return "Memory managed successfully";
    } // Memory is automatically freed here
}

}
