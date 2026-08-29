import react from "@vitejs/plugin-react";
import { configDefaults, defineConfig } from "vitest/config";

export default defineConfig({
  plugins: [react()],
  resolve: {
    tsconfigPaths: true,
  },
  test: {
    environment: "jsdom",
    setupFiles: ["./src/test/setup.ts"],
    exclude: [...configDefaults.exclude, "tests/e2e/**"],
    coverage: {
      reporter: ["text", "html"],
    },
  },
});
