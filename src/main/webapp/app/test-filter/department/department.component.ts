import { Component, OnInit } from '@angular/core';
import { IDepartment } from '../../shared/model/department.model';
import { DepartmentService } from '../../entities/department/department.service';
import { HttpResponse, HttpHeaders } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { AccountService } from 'app/core/auth/account.service';
import { flatMap } from 'rxjs/operators';

type Criterion = {
  key: string;
  value: any;
};
@Component({
  selector: 'jhi-test-filter-department',
  templateUrl: './department.component.html',
  styleUrls: ['./department.component.scss']
})
export class DepartmentComponent implements OnInit {
  population = '';
  region = '';
  departments: IDepartment[] | null = null;
  departmentsWithFilter: IDepartment[] | null = null;
  totalDepartments = 0;
  departmentsPerPage = 5;
  page!: number;
  predicate!: string;
  previousPage!: number;
  ascending!: boolean;
  all = true;

  constructor(
    private departmentService: DepartmentService,
    private activatedRoute: ActivatedRoute,
    private accountService: AccountService
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data
      .pipe(
        flatMap(
          () => this.accountService.identity(),
          data => {
            this.page = data.pagingParams.page;
            this.previousPage = data.pagingParams.page;
            this.ascending = data.pagingParams.ascending;
            this.predicate = data.pagingParams.predicate;
            this.loadAll();
          }
        )
      )
      .subscribe();
  }

  onSubmit(): void {
    this.loadWithFilter();
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

  loadAll(): void {
    this.departmentService
      .queryWithPagination({
        page: this.page - 1,
        size: this.departmentsPerPage
      })
      .subscribe((res: HttpResponse<IDepartment[]>) => this.onSuccess(res.body, res.headers));
  }

  private loadWithFilter(): void {
    this.departmentService
      .queryWithPagination({
        threshold: this.population,
        region: this.region,
        page: this.page - 1,
        size: this.departmentsPerPage
      })
      .subscribe((res: HttpResponse<IDepartment[]>) => this.onSuccess(res.body, res.headers));
  }

  private onSuccess(departments: IDepartment[] | null, headers: HttpHeaders): void {
    this.totalDepartments = Number(headers.get('X-Total-Count'));
    this.departments = departments;
  }

  pageChange(): void {
    if (this.all) {
      this.loadAll();
    } else {
      this.loadWithFilter();
    }
  }
}
