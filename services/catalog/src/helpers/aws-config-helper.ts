export function getAwsClientConfig() {
  return {
    region: process.env.AWS_REGION,
    ...(process.env.AWS_ENDPOINT && {
      endpoint: process.env.AWS_ENDPOINT,
      credentials: { accessKeyId: 'test', secretAccessKey: 'test' },
      forcePathStyle: true,
    }),
  };
}
