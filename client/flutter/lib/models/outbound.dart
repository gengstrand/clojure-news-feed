import './participant.dart';

class OutboundModel {
  final String occurred;
  final String subject;
  final String story;
  OutboundModel({
    required this.occurred,
    required this.subject,
    required this.story,
  });
}

class OutboundEnvelope {
  List<OutboundModel> build(Map<String, dynamic> payload) {
    final List<OutboundModel> retVal = [];
    payload['data']['outbound'].forEach((item) {
      retVal.add(OutboundModel(
        occurred: item['occurred'],
        subject: item['subject'],
        story: item['story'],
      ));
    });
    return retVal;
  }
}

class SearchResultModel {
  final OutboundModel outbound;
  final ParticipantModel participant;
  SearchResultModel(this.outbound, this.participant);
}

class SearchResultEnvelope {
  List<SearchResultModel> build(Map<String, dynamic> payload) {
    final List<SearchResultModel> retVal = [];
    payload['data']['search'].forEach((item) {
      final o = OutboundModel(
        occurred: item['outbound']['occurred'],
        subject: item['outbound']['subject'],
        story: item['outbound']['story'],
      );
      final p = ParticipantModel(
        id: item['participant']['id'],
	name: item['participant']['name'],
      );
      retVal.add(SearchResultModel(o, p));
    });
    return retVal;
  }
}