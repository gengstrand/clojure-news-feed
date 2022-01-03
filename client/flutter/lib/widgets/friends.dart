import 'package:flutter/material.dart';
import '../models/friends.dart';

class FriendsPost extends StatelessWidget {
  final FriendsModel friend;
  FriendsPost(this.friend);
  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(5.0),
      alignment: Alignment.centerLeft,
        child: Column(
	  mainAxisSize: MainAxisSize.min,
	  children: <Widget>[
	    Text(friend.to),
	  ],
	),
    );
  }
}