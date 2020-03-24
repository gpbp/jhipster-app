import { IDepartment } from 'app/shared/model/department.model';

export interface IRegion {
  id?: number;
  name?: string;
  prefecture?: string;
  departments?: IDepartment[];
}

export class Region implements IRegion {
  constructor(public id?: number, public name?: string, public prefecture?: string, public departments?: IDepartment[]) {}
}
