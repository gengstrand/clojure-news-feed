import 'dart:async';
import 'dart:io';
import 'dart:html';
import 'dart:ui' as ui;
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../widgets/app_drawer.dart';
import './search_outbound_results.dart';
import '../providers/auth.dart';
import '../providers/outbound.dart';

class SearchOutboundScreen extends StatefulWidget {
  static const routeName = '/search-outbound';
  
  @override
  _SearchOutboundScreenState createState() => _SearchOutboundScreenState();
}

class _SearchOutboundScreenState extends State<SearchOutboundScreen> {

  var _keywords = '';
  var _diagnostic = '';
  
  final GlobalKey<FormState> _formKey = GlobalKey();

  void _submit(AuthProvider authProvider, OutboundProvider outboundProvider) {
    if (_formKey.currentState == null) {
      return;
    }
    FormState fs = _formKey.currentState!;
    if (!fs.validate()) {
      return;
    }
    fs.save(); 
    if (_keywords != '') {
      outboundProvider.search(authProvider.getToken(), _keywords)
      .then((results) {
        Navigator.pushReplacement(
          context,
	  MaterialPageRoute(
	    builder: (context) => SearchOutboundResultsScreen(results),
	  ),
        );
      })
      .catchError((error) {
        setState(() => { _diagnostic = error.toString() });
      });
    } else {
      setState(() => { _diagnostic = 'keywords cannot be blank' });
    }
  }
  
  @override
  Widget build(BuildContext context) {
    final authProvider = Provider.of<AuthProvider>(context, listen: false);
    final outboundProvider = Provider.of<OutboundProvider>(context, listen: false);
      return Scaffold(
        appBar: AppBar(
          title: Text('Search Outbound'),
	  actions: <Widget>[
	  ],
        ),
        drawer: AppDrawer(),
        body: Container(
	  padding: EdgeInsets.all(16.0),
	  child: Form(
	    key: _formKey,
	    child: SingleChildScrollView(
	      child: Column(
	        children: <Widget>[
		  Text(_diagnostic),
		  TextFormField(
		    decoration: InputDecoration(labelText: 'keywords'),
		    onSaved: (value) => { _keywords = value! },
		  ),
		  ElevatedButton(
		    child: Text('Search'),
		    onPressed: () => { _submit(authProvider, outboundProvider) },
		  ),
		],
	      ),
	    ),
	  ),
	),
      );
  }
}