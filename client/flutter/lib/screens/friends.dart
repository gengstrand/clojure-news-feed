import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/auth.dart';
import '../providers/friends.dart';
import '../models/friends.dart';
import '../widgets/app_drawer.dart';
import '../widgets/friends.dart';

class FriendsScreen extends StatelessWidget {
  static const routeName = '/my-friends';

  @override
  Widget build(BuildContext context) {
    final authProvider = Provider.of<AuthProvider>(context, listen: false);
    return Scaffold(
      appBar: AppBar(
        title: Text('My Friends'),
      ),
      drawer: AppDrawer(),
      body: FutureBuilder(
        future: Provider.of<FriendsProvider>(context, listen: false).fetch(authProvider.getToken()),
	builder: (ctx, snapshot) {
	  if (snapshot.connectionState == ConnectionState.waiting) {
	    return Center(child: CircularProgressIndicator());
	  } else {
	    if (snapshot.error != null) {
	      return Center(
	        child: Text(snapshot.error.toString()),
	      );
	    } else {
	      final friendsPosts = snapshot.data as List<FriendsModel>;
	      return ListView.builder(
                itemCount: friendsPosts.length,
                itemBuilder: (ctx, i) => FriendsPost(friendsPosts[i]));
	    }
	  }
	},
      ),
    );
  }
}