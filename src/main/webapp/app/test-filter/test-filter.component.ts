import { Component, OnInit } from '@angular/core';
import { IDepartment } from '../shared/model/department.model';
import { DepartmentService } from '../entities/department/department.service';
import { HttpResponse } from '@angular/common/http';
import { RegionService } from '../entities/region/region.service';

type Criterion = {
  key: string;
  value: any;
};
@Component({
  selector: 'jhi-test-filter',
  templateUrl: './test-filter.component.html',
  styleUrls: ['./test-filter.component.scss']
})
export class TestFilterComponent implements OnInit {
  population = '';
  region = '';
  departments: IDepartment[] = [];

  constructor(private ds: DepartmentService, private rs: RegionService) {}

  ngOnInit(): void {}

  onSubmit(): void {
    const params = [];
    if (this.population) {
      params.push({ key: 'threshold', value: this.population });
    }
    if (this.region) {
      params.push({ key: 'region', value: this.region });
    }
    this.submitWithParams(params);
    /**
     * if (this.population !== '' && this.region !== '') {
      this.departments = [];
      this.ds.queryWithCriteria(params).subscribe((res: HttpResponse<IDepartment[]>) => {
        if (res.body) {
          res.body.forEach((value: IDepartment) => this.departments.push(value));
        }
      });
    }
     */
    // eslint-disable-next-line no-console
    // console.error(this.result);
  }

  private submitWithParams(params: Criterion[]): void {
    this.departments = [];
    this.ds.queryWithCriteria(params).subscribe((res: HttpResponse<IDepartment[]>) => {
      if (res.body) {
        res.body.forEach((value: IDepartment) => this.departments.push(value));
      }
    });
  }
}
