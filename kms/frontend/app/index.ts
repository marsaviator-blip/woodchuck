import { join } from "path";
import { FFIType, dlopen } from "bun:ffi";
import Redis from "ioredis";

// 1. COMPUTE THE PATH OUT TO THE NATIVE C++ ENGINES
// import.meta.dir is /kms/frontend/app/
// Going up two levels ("..", "..") lands us directly in the core /kms root folder!
const CORE_ROOT = join(import.meta.dir, "..", "..");
const LIB_PATH = join(CORE_ROOT, "build", "libcategorical_engine.so");

console.log(`🔌 [FFI Loader]: Mounting C++ Shared Library from: ${LIB_PATH}`);

// 2. MOUNT THE COMPILED C++ MATHEMATICAL SYMBOLS VIA FFI
const nativeLibrary = dlopen(LIB_PATH, {
  validate_composition: {
    args: [FFIType.ptr, FFIType.ptr], // Expects pointers to your C++ Morphism structs
    returns: FFIType.bool,            // Returns a raw native boolean flag from the CPU
  },
});

// 3. ESTABLISH NATIVE LINE TO THE IN-MEMORY DRAGONFLY CONTAINER
const dragonfly = new Redis({
  host: "127.0.0.1",
  port: 6379,
  lazyConnect: true,
});

// 4. BOOT THE BUN SERVER ROUTER EQUIPPED WITH A NATIVE WEBSOCKET UPGRADER
Bun.serve({
  port: 3006,
  async fetch(req, server) {
    // If the client requests a WebSocket upgrade, let Bun hand it off to the socket pool
    if (server.upgrade(req)) {
      return; // Return nothing; Bun handles the protocol upgrade automatically
    }
    return new Response("KMS Live Stream Backend Gateway Active", { status: 200 });
  },
  
  // NATIVE WEBSOCKET PROTOCOL ENGINE LAYER
  websocket: {
    // Triggers when your Vite user interface opens the connection panel
    open(ws) {
      console.log("🔗 [WebSocket]: Vite Frontend client connected to port 3006 loop.");
    },

    // Triggers every single time the user types or fires an AI event
    async message(ws, message) {
      try {
        const payload = JSON.parse(message.toString());
        console.log("📥 [WebSocket Received]:", payload);

        // CASE A: User is typing a fresh prompt or note
        if (payload.type === "USER_TYPING") {
          // Push raw keystrokes straight into high-speed Dragonfly RAM
          await dragonfly.set(`session:${payload.sessionId}:scratchpad`, payload.text);
        }

        // CASE B: User explicitly requests a Category Theory verification check
        if (payload.type === "RUN_MATH_CHECK") {
          const { morphismF, morphismG } = payload;

          // Convert standard JSON data into zero-terminated C-strings inside array buffers
          const bufF = Buffer.from(JSON.stringify(morphismF) + "\0");
          const bufG = Buffer.from(JSON.stringify(morphismG) + "\0");

          // DISPATCH THE COMPOSITION WORKLOAD DIRECTLY TO THE C++ CPU THREAD NATIVELY!
          const isValid = nativeLibrary.symbols.validate_composition(bufF, bufG);

          // Push the mathematical verdict back up the WebSocket pipe instantly
          ws.send(JSON.stringify({
            type: "MATH_CHECK_RESULT",
            compositionIsValid: isValid
          }));
        }
      } catch (err) {
        console.error("❌ Failed to process incoming socket packet:", err);
      }
    },

    close(ws) {
      console.log("❌ [WebSocket]: Client channel severed.");
    }
  }
});

// Warm up backend connection channels out-of-band
dragonfly.connect()
  .then(() => console.log("🚀 [Dragonfly Cache]: Memory channel connected! State grid active."))
  .catch((err) => console.error("❌ [Dragonfly Cache]: Connection broken. Is the container idling?", err));

console.log("⚡ [Bun Backend]: Live Power Plant listening on ws://localhost:3007");
