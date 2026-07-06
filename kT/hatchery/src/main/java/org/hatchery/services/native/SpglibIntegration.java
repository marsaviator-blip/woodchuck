package org.hatchery.services;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.nio.file.Paths;

public class SpglibIntegration {

    public static void main(String[] args) throws Throwable {
        // 1. Load the native spglib library (.so, .dll, or .dylib depending on OS)
        // Ensure libsymspg/spglib is installed on your system path
        System.loadLibrary("symspg"); 
        Linker linker = Linker.nativeLinker();
        SymbolLookup loaderLookup = SymbolLookup.loaderLookup();

        // 2. Find and bind 'spg_get_symmetry_from_database'
        MemorySegment functionAddress = loaderLookup.find("spg_get_symmetry_from_database")
                .orElseThrow(() -> new RuntimeException("Spglib function not found"));

        FunctionDescriptor descriptor = FunctionDescriptor.of(
                ValueLayout.JAVA_INT,    // Returns: num_operations (int)
                ValueLayout.ADDRESS,     // Param 1: rotations[192][3][3] pointer
                ValueLayout.ADDRESS,     // Param 2: translations[192][3] pointer
                ValueLayout.JAVA_INT     // Param 3: hall_number (int)
        );

        MethodHandle getSymmetry = linker.downcallHandle(functionAddress, descriptor);

        // 3. Define structured off-heap layouts for the Spglib data sizes
        // rotations: 192 matrices * 3 rows * 3 cols * 4 bytes (int)
        long totalRotationBytes = 192 * 3 * 3 * ValueLayout.JAVA_INT.byteSize();
        // translations: 192 vectors * 3 elements * 8 bytes (double)
        long totalTranslationBytes = 192 * 3 * ValueLayout.JAVA_DOUBLE.byteSize();

        // 4. Use a confined Arena for safe, deterministic memory lifecycle management
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment rotationsBuffer = arena.allocate(totalRotationBytes);
            MemorySegment translationsBuffer = arena.allocate(totalTranslationBytes);

            // Fetch symmetry operations for Hall number 445 (Example space group)
            int hallNumber = 445; 
            int numOps = (int) getSymmetry.invokeExact(rotationsBuffer, translationsBuffer, hallNumber);

            System.out.println("Successfully fetched " + numOps + " symmetry operations from Spglib!");

            // 5. Read out the first rotation matrix (3x3) using structural byte indexing
            System.out.println("First Rotation Matrix (3x3):");
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    // Compute the structural byte offset: (row * 3 + col) * 4 bytes
                    long offset = ((0 * 3 * 3) + (r * 3) + c) * ValueLayout.JAVA_INT.byteSize();
                    int val = rotationsBuffer.get(ValueLayout.JAVA_INT, offset);
                    System.out.print(val + " ");
                }
                System.out.println();
            }
        } // The Arena cleanly destroys and deallocates native off-heap memory arrays here
    }
}
