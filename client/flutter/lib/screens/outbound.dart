import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/auth.dart';
import '../providers/outbound.dart';
import '../models/outbound.dart';
import '../widgets/app_drawer.dart';
import '../widgets/outbound.dart';

class OutboundScreen extends StatelessWidget {
  static const routeName = '/my-posts';

  @override
  Widget build(BuildContext context) {
    final authProvider = Provider.of<AuthProvider>(context, listen: false);
    return Scaffold(
      appBar: AppBar(
        title: Text('My Stories'),
      ),
      drawer: AppDrawer(),
      body: FutureBuilder(
        future: Provider.of<OutboundProvider>(context, listen: false).fetch(authProvider.getToken()),
	builder: (ctx, snapshot) {
	  if (snapshot.connectionState == ConnectionState.waiting) {
	    return Center(child: CircularProgressIndicator());
	  } else {
	    if (snapshot.error != null) {
	      return Center(
	        child: Text(snapshot.error.toString()),
	      );
	    } else {
	      final outboundPosts = snapshot.data as List<OutboundModel>;
	      return ListView.builder(
                itemCount: outboundPosts.length,
                itemBuilder: (ctx, i) => OutboundPost(outboundPosts[i]));
	    }
	  }
	},
      ),
    );
  }
}