import 'package:flutter/material.dart';
import '../models/outbound.dart';
import '../providers/friends.dart';
import './story.dart';

class OutboundResultPost extends StatelessWidget {
  final String token;
  final String from;
  final SearchResultModel post;
  final FriendsProvider provider;
  OutboundResultPost(this.token, this.from, this.post, this.provider);

  void _friendParticipant(String to) {
    provider.add(token, from, to);
  }
  
  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(5.0),
      alignment: Alignment.centerLeft,
      child: Card(
        child: Column(
	  mainAxisSize: MainAxisSize.min,
	  children: <Widget>[
	    ListTile(
	      title: Text(post.outbound.subject, style: const TextStyle(fontWeight: FontWeight.bold)),
	      subtitle: Text(post.participant.name + '\n' + post.outbound.occurred, maxLines: 2),
	      isThreeLine: true,
	    ),
	    Story(post.outbound.story),
	    TextButton(
	      child: const Text('Friend'),
	      onPressed: () => _friendParticipant(post.participant.id),
	    ),
	  ],
	),
      ),
    );
  }
}