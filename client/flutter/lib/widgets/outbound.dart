import 'package:flutter/material.dart';
import '../models/outbound.dart';
import './story.dart';

class OutboundPost extends StatelessWidget {
  final OutboundModel post;
  OutboundPost(this.post);
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
	      subtitle: Text(post.occurred),
	    ),
	    Story(post.story),
	  ],
	),
      ),
    );
  }
}