/**
 * Mirror of the backend `MyApiResponse<T>` envelope. Every endpoint wraps its
 * payload in this shape.
 */
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
  error?: ApiErrorDetails | null;
}

export interface ApiErrorDetails {
  code?: string;
  details?: string;
  validationErrors?: unknown;
}

/** Spring Data `Page<T>` shape returned by paged endpoints. */
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
  numberOfElements: number;
}
