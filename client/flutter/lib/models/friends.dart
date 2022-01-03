class FriendsModel {
  final int id;
  final String from;
  final String to;
  FriendsModel({
    required this.id,
    required this.from,
    required this.to,
  });
}

class FriendsEnvelope {
  List<FriendsModel> build(Map<String, dynamic> payload) {
    final List<FriendsModel> retVal = [];
    payload['data']['friends'].forEach((item) {
      retVal.add(FriendsModel(
        id: 0,
        from: 'me',
        to: item['name'],
      ));
    });
    return retVal;
  }
}