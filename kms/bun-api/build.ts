import vuePlugin from "@eckidevs/bun-plugin-vue";
import { join } from "path";

const BASE_ROOT = join(import.meta.dir, ".."); 

console.log("📦 Compiling Vue JavaScript assets...");

await Bun.build({
  entrypoints: [join(BASE_ROOT, "src", "main.js")], 
  outdir: join(BASE_ROOT, "dist"), 
  target: "browser",
  plugins: [vuePlugin()], // Only parse Vue files
  define: {
    "process.env.NODE_ENV": JSON.stringify("development"),
    "__VUE_OPTIONS_API__": JSON.stringify(true),
    "__VUE_PROD_DEVTOOLS__": JSON.stringify(false),
  }
});
console.log("✅ JavaScript bundle compiled!");
