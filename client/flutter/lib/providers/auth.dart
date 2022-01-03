import 'package:flutter/material.dart';
import 'package:flutter/foundation.dart';
import './util.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class AuthProvider with ChangeNotifier {
  var _token = '';

  Map<String, String> _getQueryParams(String username, String password) {
    return {
      'grant_type': 'password',
      'username': username,
      'password': password,
      'scope': 'mobile',
    };
  }
  
  Future<void> login(String user, String pass) async {
    var r = Uri.http(Util.instance.getRawHost(), '/oauth/pcg', _getQueryParams(user, pass));
    var resp = await http.get(r);
    if (resp.statusCode == 200) {
      final b = json.decode(resp.body);
      _token = b['access_token'];
      notifyListeners();
    } else {
      throw Exception("cannot login user status $resp.statusCode");
    }
  }

  String getToken() {
    return _token;
  }
}