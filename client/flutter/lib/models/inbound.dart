import './outbound.dart';
import './participant.dart';

class InboundModel extends OutboundModel {
  final ParticipantModel from;
  InboundModel({
    required this.from,
    required occurred,
    required subject,
    required story,
  }) : super(occurred: occurred, subject: subject, story: story);
}

class InboundEnvelope {
  List<InboundModel> build(Map<String, dynamic> payload) {
    final List<InboundModel> retVal = [];
    payload['data']['inbound'].forEach((item) {
      retVal.add(InboundModel(
        from: ParticipantModel(
	  id: '',
	  name: item['from']['name'],
	),
        occurred: item['occurred'],
        subject: item['subject'],
        story: item['story'],
      ));
    });
    return retVal;
  }
}