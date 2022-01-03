import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/auth.dart';
import '../providers/inbound.dart';
import '../models/inbound.dart';
import '../widgets/app_drawer.dart';
import '../widgets/inbound.dart';

class InboundScreen extends StatelessWidget {
  static const routeName = '/my-friends-posts';

  @override
  Widget build(BuildContext context) {
    final authProvider = Provider.of<AuthProvider>(context, listen: false);
    return Scaffold(
      appBar: AppBar(
        title: Text('Stories From My Friends'),
      ),
      drawer: AppDrawer(),
      body: FutureBuilder(
        future: Provider.of<InboundProvider>(context, listen: false).fetch(authProvider.getToken()),
	builder: (ctx, snapshot) {
	  if (snapshot.connectionState == ConnectionState.waiting) {
	    return Center(child: CircularProgressIndicator());
	  } else {
	    if (snapshot.error != null) {
	      return Center(
	        child: Text(snapshot.error.toString()),
	      );
	    } else {
	      final inboundPosts = snapshot.data as List<InboundModel>;
	      return ListView.builder(
                itemCount: inboundPosts.length,
                itemBuilder: (ctx, i) => InboundPost(inboundPosts[i]));
	    }
	  }
	},
      ),
    );
  }
}