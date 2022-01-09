import 'package:flutter/material.dart';
import '../models/friends.dart';
import './util.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class FriendsProvider with ChangeNotifier {
  Future<List<FriendsModel>> fetch(String token) async {
    var r = Uri.parse(Util.instance.getHost() + '/graphql?query={friends(id:"0"){name}}');
    var resp = await http.get(r, headers: Util.instance.getHeaders(token));
    if (resp.statusCode == 200) {
      return FriendsEnvelope().build(json.decode(resp.body));
    } else {
      throw Exception("fetching friends returned status $resp.statusCode");
    }
  }
  Future<void> add(String token, String from, String to) async {
    final b = json.encode({
      'id': 0,
      'from': from,
      'to': to,
    });
    var r = Uri.parse(Util.instance.getHost() + '/participant/friends');
    var resp = await http.post(r, body: b, headers: Util.instance.getHeaders(token));
    if (resp.statusCode >= 400) {
      throw Exception("cannot add outboud status $resp.statusCode");
    }
  }
}