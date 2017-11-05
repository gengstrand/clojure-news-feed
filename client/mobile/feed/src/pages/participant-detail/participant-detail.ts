import { Component } from '@angular/core';
import { IonicPage, NavController, NavParams } from 'ionic-angular';

import { Participant } from '../../model/Participant';

@IonicPage()
@Component({
  selector: 'page-participant-detail',
  templateUrl: 'participant-detail.html'
})
export class ParticipantDetailPage {
  participant: Participant;

  constructor(public navCtrl: NavController, navParams: NavParams) {
  	this.participant =  navParams.get('participant');
  }

}
