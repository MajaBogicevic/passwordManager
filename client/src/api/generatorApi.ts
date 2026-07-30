import { httpClient } from './httpClient';

export interface GeneratePasswordParams {
  length: number;
  useUppercase: boolean;
  useLowercase: boolean;
  useDigits: boolean;
  useSymbols: boolean;
}

export const generatorApi = {
  generate: (params: GeneratePasswordParams) =>
    httpClient.get<string>('/generator/password', { params }),
};