import dayjs from 'dayjs';
import { IAuthor } from 'app/shared/model/author.model';

export interface IBook {
  id?: number;
  title?: string | null;
  descripton?: string | null;
  publicationDate?: string | null;
  bookimageContentType?: string | null;
  bookimage?: string | null;
  name?: IAuthor | null;
}

export const defaultValue: Readonly<IBook> = {};
