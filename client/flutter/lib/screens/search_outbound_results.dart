import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import './add_outbound.dart';
import '../providers/auth.dart';
import '../providers/outbound.dart';
import '../providers/friends.dart';
import '../models/outbound.dart';
import '../widgets/app_drawer.dart';
import '../widgets/outbound.dart';
import '../widgets/outbound_result.dart';

class SearchOutboundResultsScreen extends StatelessWidget {
  final List<SearchResultModel> results;

  SearchOutboundResultsScreen(this.results);

  @override
  Widget build(BuildContext context) {
    final authProvider = Provider.of<AuthProvider>(context, listen: false);
    final friendsProvider = Provider.of<FriendsProvider>(context, listen: false);
    return Scaffold(
      appBar: AppBar(
        title: Text('Search Results'),
      ),
      drawer: AppDrawer(),
      body: ListView.builder(
        itemCount: results.length,
        itemBuilder: (ctx, i) => OutboundResultPost(authProvider.getToken(), authProvider.getId(), results[i], friendsProvider),
      ),
    );
  }
}