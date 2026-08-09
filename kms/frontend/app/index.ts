Bun.serve({
  port: 3006,
  async fetch(req) {
    const url = new URL(req.url);

    // If the user visits the homepage, send them the frontend file
    if (url.pathname === "/") {
      return new Response(Bun.file("../frontend/index.html"));
    }

    // Fallback for API routes
    return new Response("KMS Backend Engine Active", { status: 200 });
  },
});

console.log("🚀 KMS Workspace Active at http://localhost:3006");
