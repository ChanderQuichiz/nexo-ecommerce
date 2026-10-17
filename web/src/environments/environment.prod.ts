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
  production: true,
  apiUrl: runtimeConfig.apiUrl || '/api',
  shippingFee: 0,
  stripePublicKey: runtimeConfig.stripePublicKey || '',
};
