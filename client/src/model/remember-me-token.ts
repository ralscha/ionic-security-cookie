export interface RememberMeToken {
  series: string;
  tokenDate: number;
  ipAddress: string;
  userAgent: string;
  ua_browser?: string;
  ua_device?: string;
  ua_os?: string;
}
