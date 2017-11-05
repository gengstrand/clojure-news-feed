import { Component } from '@angular/core';
import { IonicPage, ModalController, NavController } from 'ionic-angular';
import { User } from '../../providers/providers';
import { Inbound } from '../../model/Inbound';
import { InboundApi } from '../../providers/providers';
import { OutboundApi } from '../../providers/providers';

@IonicPage()
@Component({
  selector: 'page-list-master',
  templateUrl: 'list-master.html'
})
export class ListMasterPage {
  currentInbound: Inbound[];

  constructor(public navCtrl: NavController, public inboundApi: InboundApi, public outboundApi: OutboundApi, public user: User, public modalCtrl: ModalController) {
    this.inboundApi.getInbound(this.user._user.id).subscribe(f => {
        this.currentInbound =  f;
    });
  }

  /**
   * The view loaded, let's query our items for the list
   */
  ionViewDidLoad() {
  }

  /**
   * Prompt the user to add a new item. This shows our ItemCreatePage in a
   * modal and then adds the new item to our data source if the user created one.
   */
  addOutbound() {
    let addModal = this.modalCtrl.create('OutboundCreatePage');
    addModal.onDidDismiss(data => {
    	if (data) {
	   this.outboundApi.addOutbound(data);
	} else {
	   console.log("data empty");
	}
    });
    addModal.present();
  }

  /**
   * Navigate to the detail page for this item.
   */
  openInbound(inbound: Inbound) {
    this.navCtrl.push('InboundDetailPage', {
      inbound: inbound
    });
  }
}
