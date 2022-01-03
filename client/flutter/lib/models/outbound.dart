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