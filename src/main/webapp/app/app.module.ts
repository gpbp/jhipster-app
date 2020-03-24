import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import './vendor';
import { MyapplicationSharedModule } from 'app/shared/shared.module';
import { MyapplicationCoreModule } from 'app/core/core.module';
import { MyapplicationAppRoutingModule } from './app-routing.module';
import { MyapplicationHomeModule } from './home/home.module';
import { MyapplicationEntityModule } from './entities/entity.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { MainComponent } from './layouts/main/main.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { FooterComponent } from './layouts/footer/footer.component';
import { PageRibbonComponent } from './layouts/profiles/page-ribbon.component';
import { ErrorComponent } from './layouts/error/error.component';
import { TestFilterModule } from './test-filter/test-filter.module';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@NgModule({
  imports: [
    BrowserModule,
    MyapplicationSharedModule,
    MyapplicationCoreModule,
    MyapplicationHomeModule,
    // jhipster-needle-angular-add-module JHipster will add new module here
    MyapplicationEntityModule,
    MyapplicationAppRoutingModule,
    TestFilterModule,
    FormsModule,
    CommonModule
  ],
  declarations: [MainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, FooterComponent],
  bootstrap: [MainComponent]
})
export class MyapplicationAppModule {}
