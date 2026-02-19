import { test, expect } from '@playwright/test';

test('basic task flow', async ({ page }) => {
  await page.goto('http://localhost:4200');
  await expect(page).toHaveTitle(/Tasks|Task/);

  // Click add (may vary depending on template)
  const add = page.getByRole('button', { name: /Add Task/i });
  if (await add.count()) {
    await add.first().click();
    await page.fill('input[formcontrolname="title"]', 'E2E Task');
    await page.click('button[type="submit"]');
    await page.waitForURL('**/');
    await expect(page.locator('text=E2E Task')).toHaveCount(1);
  }
});
