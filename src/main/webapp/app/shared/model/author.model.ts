import dayjs from 'dayjs';

export interface IAuthor {
  id?: number;
  nameauthor?: string | null;
  birthDate?: string | null;
  description?: string | null;
}

export const defaultValue: Readonly<IAuthor> = {};
