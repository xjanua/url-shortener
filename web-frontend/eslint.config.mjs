import { defineConfig, globalIgnores } from "eslint/config";
import nextVitals from "eslint-config-next/core-web-vitals";
import nextTs from "eslint-config-next/typescript";
import prettier from "eslint-config-prettier/flat";

const eslintConfig = defineConfig([
  ...nextVitals,
  ...nextTs,
  prettier,
  {
    files: ["src/features/**/*.{ts,tsx}"],
    rules: {
      "no-restricted-imports": [
        "error",
        {
          patterns: [
            {
              group: ["@/app/*"],
              message: "Features must not depend on the routing layer.",
            },
          ],
        },
      ],
    },
  },
  {
    files: ["src/components/ui/**/*.{ts,tsx}", "src/lib/**/*.{ts,tsx}"],
    rules: {
      "no-restricted-imports": [
        "error",
        {
          patterns: [
            {
              group: ["@/app/*", "@/features/*"],
              message:
                "Shared infrastructure must not depend on routes or domain features.",
            },
          ],
        },
      ],
    },
  },
  {
    files: ["src/**/*.{ts,tsx}"],
    ignores: ["src/lib/api/client.ts"],
    rules: {
      "no-restricted-globals": [
        "error",
        {
          name: "fetch",
          message:
            "Use apiRequest from @/lib/api/client so every request calls Spring directly through the global client.",
        },
      ],
    },
  },
  // Override default ignores of eslint-config-next.
  globalIgnores([
    // Default ignores of eslint-config-next:
    ".next/**",
    "out/**",
    "build/**",
    "next-env.d.ts",
  ]),
]);

export default eslintConfig;
