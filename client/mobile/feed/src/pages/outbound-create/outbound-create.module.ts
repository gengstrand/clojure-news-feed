import { NgModule } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { IonicPageModule } from 'ionic-angular';

import { OutboundCreatePage } from './outbound-create';

@NgModule({
  declarations: [
    OutboundCreatePage,
  ],
  imports: [
    IonicPageModule.forChild(OutboundCreatePage),
    TranslateModule.forChild()
  ],
  exports: [
    OutboundCreatePage
  ]
})
export class OutboundCreatePageModule { }
