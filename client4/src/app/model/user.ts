export interface User {
  firstName: string;
  lastName: string;
  email: string;
  username: string;
  password: string;
  oldPassword?: string;

  enabled?: boolean;
  failedLogins?: number;
  lockedOutUntil?: number;
  lastAccess?: number;
  passwordResetTokenValidUntil?: number;
  authorities?: string[];
}
