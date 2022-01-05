import 'package:flutter/material.dart';
import '../screens/outbound.dart';
import '../screens/add_outbound.dart';
import '../screens/inbound.dart';
import '../screens/friends.dart';

class AppDrawer extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Drawer(
      child: Column(
        children: <Widget>[
	  AppBar(
	    title: Text('News Feed'),
	    automaticallyImplyLeading: false,
	  ),
 	  Divider(),
	  ListTile(
	    leading: Icon(
              Icons.record_voice_over,
            ),
	    title: Text('My Posts'),
	    onTap: () {
	      Navigator.of(context).pushReplacementNamed(OutboundScreen.routeName);
	    },
	  ),
 	  Divider(),
	  ListTile(
	    leading: Icon(
              Icons.record_voice_over,
            ),
	    title: Text('Post New Item'),
	    onTap: () {
	      Navigator.of(context).pushReplacementNamed(AddOutboundScreen.routeName);
	    },
	  ),
	  Divider(),
	  ListTile(
	    leading: Icon(
              Icons.hearing,
            ),
	    title: Text('My Friends Posts'),
	    onTap: () {
	      Navigator.of(context).pushReplacementNamed(InboundScreen.routeName);
	    },
	  ),
	  Divider(),
	  ListTile(
	    leading: Icon(
              Icons.people,
            ),
	    title: Text('My Friends'),
	    onTap: () {
	      Navigator.of(context).pushReplacementNamed(FriendsScreen.routeName);
	    },
	  ),
	],
      ),
    );
  }
}


