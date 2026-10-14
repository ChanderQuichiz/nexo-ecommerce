import { readFile, writeFile } from 'node:fs/promises';
import { resolve } from 'node:path';

const required = process.argv.includes('--required');
const envFile = process.argv.find((argument) => argument.startsWith('--env='))?.slice(6);

if (envFile) {
  const envPath = resolve('..', envFile);
  const contents = await readFile(envPath, 'utf8');

  for (const line of contents.split(/\r?\n/)) {
    const match = line.match(/^\s*([A-Z][A-Z0-9_]*)\s*=\s*(.*)\s*$/);
    if (!match || process.env[match[1]]) {
      continue;
    }

    process.env[match[1]] = match[2].replace(/^(['"])(.*)\1$/, '$2');
  }
}

const apiUrl = process.env.WEB_API_URL || '/api';
const stripePublicKey = process.env.STRIPE_PUBLIC_KEY || '';

if (required && !process.env.WEB_API_URL) {
  throw new Error('WEB_API_URL must be set for a production build');
}

if (required && !process.env.STRIPE_PUBLIC_KEY) {
  throw new Error('STRIPE_PUBLIC_KEY must be set for a production build');
}

const runtimeConfig = `window.__NEXO_CONFIG__ = ${JSON.stringify({
  apiUrl,
  stripePublicKey,
}, null, 2)};
`;

await writeFile('public/runtime-config.js', runtimeConfig, 'utf8');
