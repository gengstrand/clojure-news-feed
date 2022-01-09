import 'package:flutter/material.dart';
import '../models/inbound.dart';
import './story.dart';

class InboundPost extends StatelessWidget {
  final InboundModel post;
  InboundPost(this.post);
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
	      title: Text(post.subject, style: const TextStyle(fontWeight: FontWeight.bold)),
	      subtitle: Text(post.from.name + '\n' + post.occurred, maxLines: 2),
	      isThreeLine: true,
	    ),
	    Story(post.story),
	  ],
	),
      ),
    );
  }
}