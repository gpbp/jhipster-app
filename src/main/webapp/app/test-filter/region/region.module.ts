import { NgModule } from '@angular/core';
import { RegionComponent } from './region.component';
import { RouterModule } from '@angular/router';
import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: '',
        component: RegionComponent,
        data: {
          authorities: [Authority.USER]
        },
        canActivate: [UserRouteAccessService]
      }
    ])
  ],
  declarations: [RegionComponent]
})
export class RegionModule {}
