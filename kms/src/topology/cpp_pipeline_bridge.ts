/**
 * Service to orchestrate the lifecycle and memory streaming 
 * between the Bun Server and the C++ Mathematical Pipeline.
 */
export class CppPipelineBridge {
  private binaryPath: string;

  constructor() {
    // Path to the compiled C++ executable on the laptop
    this.binaryPath = "./bin/category_processor"; 
  }

  /**
   * Executes the C++ binary, streams data to stdin, and collects stdout.
   * @param isServerTier Boolean indicating if we use Tier 2 processing power.
   * @param payload The structured json data from your validation step.
   */
  async streamToMatrixPipeline(isServerTier: boolean, payload: object): Promise<string> {
    // 1. Configure the runtime flags based on your affordability tier selection
    const modeFlag = isServerTier ? "--mode=heavyweight" : "--mode=lightweight";
    const serializedJson = JSON.stringify(payload);

    try {
      // 2. Spawn the C++ process with open standard I/O pipes
      const process = Bun.spawn([this.binaryPath, modeFlag], {
        stdin: "pipe",   // Open write channel to pass JSON string in memory
        stdout: "pipe",  // Open read channel to listen for calculated matrix outputs
        stderr: "pipe",  // Capture system errors without crashing the main Bun server
      });

      // 3. Stream the JSON string data directly into the C++ process stdin
      const writer = process.stdin.writer();
      writer.write(serializedJson);
      writer.write("\n"); // Append newline as a clean termination signal for C++ text scanners
      await writer.flush();
      writer.end(); // Close stdin to tell C++ there is no more incoming data

      // 4. Await the process exit code in the background
      const exitCode = await process.exited;
      
      // 5. Read the mathematical results from stdout
      const stdoutResponse = await new Response(process.stdout).text();

      if (exitCode !== 0) {
        const stderrResponse = await new Response(process.stderr).text();
        throw new Error(`C++ Pipeline crashed [Exit ${exitCode}]: ${stderrResponse}`);
      }

      return stdoutResponse;

    } catch (error) {
      console.error("Fatal Error inside Bun/C++ Bridge execution loop:", error);
      throw error;
    }
  }
}

/**
 * Service to orchestrate the lifecycle and memory streaming 
 * between the Bun Server and the C++ Mathematical Pipeline.
 */
export class CppPipelineBridge {
  private binaryPath: string;

  constructor() {
    // Path to the compiled C++ executable on the laptop
    this.binaryPath = "./bin/category_processor"; 
  }

  /**
   * Executes the C++ binary, streams data to stdin, and collects stdout.
   * @param isServerTier Boolean indicating if we use Tier 2 processing power.
   * @param payload The structured json data from your validation step.
   */
  async streamToMatrixPipeline(isServerTier: boolean, payload: object): Promise<string> {
    // 1. Configure the runtime flags based on your affordability tier selection
    const modeFlag = isServerTier ? "--mode=heavyweight" : "--mode=lightweight";
    const serializedJson = JSON.stringify(payload);

    try {
      // 2. Spawn the C++ process with open standard I/O pipes
      const process = Bun.spawn([this.binaryPath, modeFlag], {
        stdin: "pipe",   // Open write channel to pass JSON string in memory
        stdout: "pipe",  // Open read channel to listen for calculated matrix outputs
        stderr: "pipe",  // Capture system errors without crashing the main Bun server
      });

      // 3. Stream the JSON string data directly into the C++ process stdin
      const writer = process.stdin.writer();
      writer.write(serializedJson);
      writer.write("\n"); // Append newline as a clean termination signal for C++ text scanners
      await writer.flush();
      writer.end(); // Close stdin to tell C++ there is no more incoming data

      // 4. Await the process exit code in the background
      const exitCode = await process.exited;
      
      // 5. Read the mathematical results from stdout
      const stdoutResponse = await new Response(process.stdout).text();

      if (exitCode !== 0) {
        const stderrResponse = await new Response(process.stderr).text();
        throw new Error(`C++ Pipeline crashed [Exit ${exitCode}]: ${stderrResponse}`);
      }

      return stdoutResponse;

    } catch (error) {
      console.error("Fatal Error inside Bun/C++ Bridge execution loop:", error);
      throw error;
    }
  }
}

