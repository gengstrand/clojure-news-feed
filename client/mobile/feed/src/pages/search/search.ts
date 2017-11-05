import { Component } from '@angular/core';
import { IonicPage, NavController, NavParams } from 'ionic-angular';

import { Outbound } from '../../model/Outbound';
import { Participant } from '../../model/Participant';
import { OutboundApi } from '../../providers/providers';
import { ParticipantApi } from '../../providers/providers';

@IonicPage()
@Component({
  selector: 'page-search',
  templateUrl: 'search.html'
})
export class SearchPage {

  currentParticipant: any = [];

  constructor(public navCtrl: NavController, public navParams: NavParams, public outboundApi: OutboundApi, public participantApi: ParticipantApi) { }

  /**
   * Perform a service for the proper items.
   */
  getItems(ev) {
    this.currentParticipant = [];
    let val = ev.target.value;
    if (!val || !val.trim()) {
      return;
    }
    let searchResults = this.outboundApi.searchOutbound(val);
    searchResults.subscribe(pids => {
    	for (let pid of pids) {
	    this.participantApi.getParticipant(pid).subscribe(p => {
	    	this.currentParticipant.push(p);
	    });
    	}
    });
  }

  /**
   * Navigate to the detail page for this item.
   */
  openItem(participant: Participant) {
    this.navCtrl.push('ParticipantDetailPage', {
      participant: participant
    });
  }

}
