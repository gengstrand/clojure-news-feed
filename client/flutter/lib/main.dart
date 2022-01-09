import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import './screens/signin.dart';
import './screens/outbound.dart';
import './screens/inbound.dart';
import './screens/friends.dart';
import './screens/add_outbound.dart';
import './screens/search_outbound.dart';
import './providers/auth.dart';
import './providers/outbound.dart';
import './providers/inbound.dart';
import './providers/friends.dart';
import './models/outbound.dart';
import './models/inbound.dart';
import './models/friends.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  
  const MyApp({Key? key}) : super(key: key);
  
  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        ChangeNotifierProvider<OutboundProvider>(
	  create: (_) => OutboundProvider(),
	),
        ChangeNotifierProvider<InboundProvider>(
	  create: (_) => InboundProvider(),
	),
        ChangeNotifierProvider<FriendsProvider>(
	  create: (_) => FriendsProvider(),
	),
        ChangeNotifierProvider<AuthProvider>(
	  create: (_) => AuthProvider(),
	),
      ],
      child: MaterialApp(
        title: 'News Feed',
        theme: ThemeData(
          primarySwatch: Colors.blue,
	  cardTheme: CardTheme(
	    elevation: 2.0,
	    margin: EdgeInsets.all(5.0),
	  ),
        ),
        home: SigninWebView(),
	routes: {
	  OutboundScreen.routeName: (ctx) => OutboundScreen(),
	  InboundScreen.routeName: (ctx) => InboundScreen(),
	  FriendsScreen.routeName: (ctx) => FriendsScreen(),
	  AddOutboundScreen.routeName: (ctx) => AddOutboundScreen(),
	  SearchOutboundScreen.routeName: (ctx) => SearchOutboundScreen(),
	},
      ),
    );
  }
}
