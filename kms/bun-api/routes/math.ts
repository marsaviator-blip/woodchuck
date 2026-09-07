import { Elysia, t } from 'elysia';
import { cc } from 'bun:ffi'; // Bun's native high-speed C++ FFI

export const mathRoutes = new Elysia({ prefix: '/math' })
  .post('/compute', async ({ body }) => {
    const { x, y } = body;

    // Example: Interfacing with a native C++ compiled binary/library
    // const lib = cc("./math_module.so", { add: { args: ["int", "int"], returns: "int" } });
    // const result = lib.symbols.add(x, y);

    return { result: x + y }; // Mock calculation
  }, {
    body: t.Object({
      x: t.Number(),
      y: t.Number()
    })
  });
