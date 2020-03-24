export interface IDepartment {
  id?: number;
  name?: string;
  population?: number;
  department_number?: string;
  regionId?: number;
  regionName?: string;
}

export class Department implements IDepartment {
  constructor(
    public id?: number,
    public name?: string,
    public population?: number,
    public department_number?: string,
    public regionId?: number
  ) {}
}
