import 'dart:async';
import 'dart:io';
import 'dart:html';
import 'dart:ui' as ui;
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../widgets/app_drawer.dart';
import '../providers/auth.dart';

class SigninWebView extends StatefulWidget {
  @override
  _SigninWebViewState createState() => _SigninWebViewState();
}

class _SigninWebViewState extends State<SigninWebView> {

  var _userName = '';
  var _password = '';
  final GlobalKey<FormState> _formKey = GlobalKey();

  void _submit(AuthProvider authProvider) {
    if (_formKey.currentState == null) {
      return;
    }
    FormState fs = _formKey.currentState!;
    if (!fs.validate()) {
      return;
    }
    fs.save(); 
    if (_userName != '' && _password != '') {
      print('calling login');
      authProvider.login(_userName, _password);
    } else {
      print('user and pass not set');
    }   
  }
  
  @override
  Widget build(BuildContext context) {
    final authProvider = Provider.of<AuthProvider>(context, listen: false);
    if (authProvider.getToken() == '') {
      return Scaffold(
        appBar: AppBar(
          title: Text('Please sign in'),
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
		  TextFormField(
		    decoration: InputDecoration(labelText: 'user name'),
		    onSaved: (value) => { _userName = value! },
		  ),
		  TextFormField(
		    decoration: InputDecoration(labelText: 'password'),
		    obscureText: true,
		    onSaved: (value) => { _password = value! },
		  ),
		  ElevatedButton(
		    child: Text('login'),
		    onPressed: () => { _submit(authProvider) },
		  ),
		],
	      ),
	    ),
	  ),
	),
      );
    } else {
      return Scaffold(
        appBar: AppBar(
          title: Text('Welcome'),
	  actions: <Widget>[
	  ],
        ),
	drawer: AppDrawer(),
        body: Padding(
	  padding: EdgeInsets.all(5),
	  child: FittedBox(
	    child: Text(
              'Welcome to this rudimentary news feed application written in dart and flutter.',
            ),
	  ),
	),
      );
    }
  }
}