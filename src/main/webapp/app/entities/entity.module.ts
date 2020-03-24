import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'region',
        loadChildren: () => import('./region/region.module').then(m => m.MyapplicationRegionModule)
      },
      {
        path: 'department',
        loadChildren: () => import('./department/department.module').then(m => m.MyapplicationDepartmentModule)
      }
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ])
  ]
})
export class MyapplicationEntityModule {}
