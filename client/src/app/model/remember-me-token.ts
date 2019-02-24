export interface RememberMeToken {
  series: string;
  tokenDate: number;
  ipAddress: string;
  userAgent: string;
  uaBrowser?: string;
  uaDevice?: string;
  uaOs?: string;
}
