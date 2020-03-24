import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { DepartmentComponent } from './department.component';
import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MyapplicationSharedModule } from 'app/shared/shared.module';
import { DepartmentListComponent } from './department-list/department-list.component';
import { DepartmentFilteredComponent } from './department-filtered/department-filtered.component';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: '',
        component: DepartmentComponent,
        data: {
          authorities: [Authority.USER],
          pagingParams: {
            page: 1,
            ascending: true,
            predicate: 'id'
          }
        },
        canActivate: [UserRouteAccessService]
      }
    ]),
    CommonModule,
    FormsModule,
    MyapplicationSharedModule
  ],
  declarations: [DepartmentComponent, DepartmentListComponent, DepartmentFilteredComponent]
})
export class DepartmentModule {}
