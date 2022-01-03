class ParticipantModel {
  final String id;
  final String name;
  String link = '';

  ParticipantModel({
    required this.id,
    required this.name,
  }) {
    this.link = '/participant/' + this.id;
  }
}