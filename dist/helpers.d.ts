import { IntentError, IntentResponse } from "./interface";
export declare const getError: (message: string, error?: any) => IntentError;
export declare const getResponse: (response: any) => IntentResponse;
