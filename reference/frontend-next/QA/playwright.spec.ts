import { test, expect } from "@playwright/test";
test("brand page loads with accessible CTA", async ({ page }) => {
  await page.goto("/brand");
  await expect(page.getByRole("link", { name: "CLAIM SEAT" })).toBeVisible();
});