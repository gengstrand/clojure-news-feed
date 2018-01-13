import { Component } from '@angular/core';
import { IonicPage, ModalController, NavController, ToastController } from 'ionic-angular';
import { User } from '../../providers/providers';
import { Inbound } from '../../model/Inbound';
import { Outbound } from '../../model/Outbound';
import { InboundApi } from '../../providers/providers';
import { OutboundApi } from '../../providers/providers';

@IonicPage()
@Component({
  selector: 'page-list-master',
  templateUrl: 'list-master.html'
})
export class ListMasterPage {
  currentInbound: Inbound[];

  constructor(public navCtrl: NavController, public toastCtrl: ToastController, public inboundApi: InboundApi, public outboundApi: OutboundApi, public user: User, public modalCtrl: ModalController) {
    this.inboundApi.getInbound(this.user._user.id).subscribe(f => {
        this.currentInbound =  f;
    });
  }

  /**
   * Prompt the user to add a new item. This shows our ItemCreatePage in a
   * modal and then adds the new item to our data source if the user created one.
   */
  addOutbound() {
    console.log('showing modal for create outbound');
    let addModal = this.modalCtrl.create('OutboundCreatePage');
    addModal.onDidDismiss(data => {
    	var outbound: Outbound = {
	    from: Number(this.user._user.id),
	    subject: data.subject,
	    story: data.story
	};
	this.outboundApi.addOutbound(outbound).subscribe(o => {
	    console.log("outbound created");
	    let toast = this.toastCtrl.create({
	    	message: 'Item was added successfully',
		duration: 3000,
		position: 'top'
  	    });
	    toast.present();
	});
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
