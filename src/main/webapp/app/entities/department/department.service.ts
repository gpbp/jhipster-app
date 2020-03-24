import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IDepartment } from 'app/shared/model/department.model';

type EntityResponseType = HttpResponse<IDepartment>;
type EntityArrayResponseType = HttpResponse<IDepartment[]>;
type Criterion = {
  key: string;
  value: any;
};
@Injectable({ providedIn: 'root' })
export class DepartmentService {
  public resourceUrl = SERVER_API_URL + 'api/departments';

  constructor(protected http: HttpClient) {}

  create(department: IDepartment): Observable<EntityResponseType> {
    return this.http.post<IDepartment>(this.resourceUrl, department, { observe: 'response' });
  }

  update(department: IDepartment): Observable<EntityResponseType> {
    return this.http.put<IDepartment>(this.resourceUrl, department, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IDepartment>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDepartment[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  queryWithPagination(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDepartment[]>(this.resourceUrl + '/getDepartmentsWithPagination', { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  queryWithCriteria(criteria: Criterion[]): Observable<EntityArrayResponseType> {
    const options = this.createRequestParams(criteria);
    return this.http.get<IDepartment[]>(this.resourceUrl + '/test-filters' + options, { observe: 'response' });
  }

  queryForPageObject(criteria: Criterion[]): Observable<EntityArrayResponseType> {
    const options = this.createRequestParams(criteria);
    return this.http.get<IDepartment[]>(this.resourceUrl + '/test-filters' + options, { observe: 'response' });
  }

  queryWithDirectLink(link: string): Observable<EntityArrayResponseType> {
    return this.http.get<IDepartment[]>(link, { observe: 'response' });
  }

  private createRequestParams(criteria: Criterion[]): string {
    let options = '';
    if (criteria.length > 0) {
      options += '?';
      criteria.forEach((criterion: Criterion) => (options += criterion.key + '=' + criterion.value + '&'));
      options = options.substring(0, options.length - 1);
    }
    return options;
  }
}
