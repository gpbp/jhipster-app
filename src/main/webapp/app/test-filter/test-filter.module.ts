import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { TestFilterComponent } from './test-filter.component';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'region',
        loadChildren: () => import('./region/region.module').then(m => m.RegionModule)
      },
      {
        path: 'department',
        loadChildren: () => import('./department/department.module').then(m => m.DepartmentModule)
      }
    ]),
    FormsModule,
    CommonModule
  ],
  declarations: [TestFilterComponent]
})
export class TestFilterModule {}
