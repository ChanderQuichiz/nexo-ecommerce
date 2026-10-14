declare global {
  interface Window {
    __NEXO_CONFIG__?: {
      apiUrl?: string;
      stripePublicKey?: string;
    };
  }
}

const runtimeConfig = window.__NEXO_CONFIG__ ?? {};

export const environment = {
  production: false,
  apiUrl: runtimeConfig.apiUrl || 'http://localhost:8082/api',
  shippingFee: 0,
  stripePublicKey: runtimeConfig.stripePublicKey || '',
};
