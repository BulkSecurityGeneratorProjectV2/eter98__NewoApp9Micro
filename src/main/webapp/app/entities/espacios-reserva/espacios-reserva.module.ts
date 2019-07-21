import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { JhiLanguageHelper } from 'app/core';

import { NewoApp9MicroSharedModule } from 'app/shared';
import {
  EspaciosReservaComponent,
  EspaciosReservaDetailComponent,
  EspaciosReservaUpdateComponent,
  EspaciosReservaDeletePopupComponent,
  EspaciosReservaDeleteDialogComponent,
  espaciosReservaRoute,
  espaciosReservaPopupRoute
} from './';

const ENTITY_STATES = [...espaciosReservaRoute, ...espaciosReservaPopupRoute];

@NgModule({
  imports: [NewoApp9MicroSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    EspaciosReservaComponent,
    EspaciosReservaDetailComponent,
    EspaciosReservaUpdateComponent,
    EspaciosReservaDeleteDialogComponent,
    EspaciosReservaDeletePopupComponent
  ],
  entryComponents: [
    EspaciosReservaComponent,
    EspaciosReservaUpdateComponent,
    EspaciosReservaDeleteDialogComponent,
    EspaciosReservaDeletePopupComponent
  ],
  providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class NewoApp9MicroEspaciosReservaModule {
  constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
    this.languageHelper.language.subscribe((languageKey: string) => {
      if (languageKey !== undefined) {
        this.languageService.changeLanguage(languageKey);
      }
    });
  }
}
