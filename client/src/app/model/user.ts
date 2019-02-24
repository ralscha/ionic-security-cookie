export interface User {
  firstName: string;
  lastName: string;
  email: string;
  userName: string;
  password: string;
  oldPassword?: string;

  enabled?: boolean;
  failedLogins?: number;
  lockedOut?: boolean;
  lastAccess?: number;
  passwordResetTokenValidUntil?: number;
  authorities?: string[];
}
