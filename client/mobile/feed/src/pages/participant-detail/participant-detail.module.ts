import { NgModule } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { IonicPageModule } from 'ionic-angular';

import { ParticipantDetailPage } from './participant-detail';

@NgModule({
  declarations: [
    ParticipantDetailPage,
  ],
  imports: [
    IonicPageModule.forChild(ParticipantDetailPage),
    TranslateModule.forChild()
  ],
  exports: [
    ParticipantDetailPage
  ]
})
export class ParticipantDetailPageModule { }
