import 'dart:async';
import 'dart:io';
import 'dart:html';
import 'dart:ui' as ui;
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/auth.dart';
import '../providers/outbound.dart';

class AddOutboundScreen extends StatefulWidget {
  static const routeName = '/add-outbound';
  
  @override
  _AddOutboundScreenState createState() => _AddOutboundScreenState();
}

class _AddOutboundScreenState extends State<AddOutboundScreen> {

  var _subject = '';
  var _story = '';
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
    if (_subject != '' && _story != '') {
      outboundProvider.add(authProvider.getToken(), _subject, _story).then((_) {
        Navigator.pop(context);
      })
      .catchError((error) {
        setState(() => { _diagnostic = error.toString() });
      });
    } else {
      setState(() => { _diagnostic = 'neither subject nor story can be blank' });
    }
  }
  
  @override
  Widget build(BuildContext context) {
    final authProvider = Provider.of<AuthProvider>(context, listen: false);
    final outboundProvider = Provider.of<OutboundProvider>(context, listen: false);
      return Scaffold(
        appBar: AppBar(
          title: Text('Add Outbound'),
	  actions: <Widget>[
	  ],
        ),
        body: Container(
	  padding: EdgeInsets.all(16.0),
	  child: Form(
	    key: _formKey,
	    child: SingleChildScrollView(
	      child: Column(
	        children: <Widget>[
		  Text(_diagnostic),
		  TextFormField(
		    decoration: InputDecoration(labelText: 'subject'),
		    onSaved: (value) => { _subject = value! },
		  ),
		  TextFormField(
		    decoration: InputDecoration(labelText: 'story'),
		    onSaved: (value) => { _story = value! },
		  ),
		  Row(
		    crossAxisAlignment: CrossAxisAlignment.start,
		    children: <Widget>[
		      ElevatedButton(
		        child: Text('Cancel'),
		        onPressed: () => { Navigator.pop(context) },
		      ),
		      ElevatedButton(
		        child: Text('Add'),
		        onPressed: () => { _submit(authProvider, outboundProvider) },
		      ),
		    ],
		  ),
		],
	      ),
	    ),
	  ),
	),
      );
  }
}