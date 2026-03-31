import { Component, OnInit } from '@angular/core';
import { NgClass } from '@angular/common';
import {
  BreadcrumbComponent,
  BreadcrumbItemComponent,
  BreadcrumbRouterComponent,
  CardBodyComponent,
  CardComponent,
  CardHeaderComponent,
  ColComponent,
  RowComponent,
  TextColorDirective
} from '@coreui/angular';

@Component({
    templateUrl: './breadcrumbs.component.html',
    styleUrls: ['./breadcrumbs.component.scss'],
    imports: [RowComponent, ColComponent, TextColorDirective, CardComponent, CardHeaderComponent, CardBodyComponent, DocsExampleComponent, BreadcrumbComponent, BreadcrumbItemComponent, NgClass, BreadcrumbRouterComponent]
})
export class BreadcrumbsComponent implements OnInit {
  public items = <any>[];

  constructor() {}

  ngOnInit(): void {
    this.items = [
      { label: 'Home', url: '/home', attributes: { title: 'Home' } },
      { label: 'Data', url: '/home' }     
    ];

    setTimeout(() => {
      this.items = [      
        { label: 'Data', url: '/home' },      
        { label: 'Home', url: '/home', attributes: { title: 'Home' } }
      ];
    }, 5000);
  }
}
