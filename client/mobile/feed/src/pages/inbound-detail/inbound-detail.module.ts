import { NgModule } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { IonicPageModule } from 'ionic-angular';

import { InboundDetailPage } from './inbound-detail';

@NgModule({
  declarations: [
    InboundDetailPage,
  ],
  imports: [
    IonicPageModule.forChild(InboundDetailPage),
    TranslateModule.forChild()
  ],
  exports: [
    InboundDetailPage
  ]
})
export class InboundDetailPageModule { }
