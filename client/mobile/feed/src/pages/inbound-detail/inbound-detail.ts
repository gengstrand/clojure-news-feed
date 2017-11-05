import { Component } from '@angular/core';
import { IonicPage, NavController, NavParams } from 'ionic-angular';

import { Inbound } from '../../model/Inbound';

@IonicPage()
@Component({
  selector: 'page-inbound-detail',
  templateUrl: 'inbound-detail.html'
})
export class InboundDetailPage {
  inbound: Inbound;

  constructor(public navCtrl: NavController, navParams: NavParams) {
    this.inbound = navParams.get('inbound');
  }

}
