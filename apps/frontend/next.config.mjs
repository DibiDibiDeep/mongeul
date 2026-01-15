import withPWA from 'next-pwa';

const S3_DOMAIN = process.env.NEXT_PUBLIC_S3_DOMAIN;

// S3_DOMAIN이 없으면 아예 패턴에서 제외
const remotePatterns = [
  ...(S3_DOMAIN
    ? [{
        protocol: 'https',
        hostname: S3_DOMAIN,
        pathname: '/**',
      }]
    : []),
  {
    protocol: 'https',
    hostname: 'mongeul.com',
    pathname: '/**',
  },
];

/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  async rewrites() {
    return [];
  },
  images: {
    remotePatterns, // remotePatterns만 사용(권장)
  },
  pageExtensions: ['tsx', 'ts', 'jsx', 'js', 'mdx'],
  trailingSlash: false,
};

const pwaConfig = {
  dest: 'public',
  disable: process.env.NODE_ENV === 'development',
  register: true,
  skipWaiting: true,
};

export default withPWA(pwaConfig)(nextConfig);
