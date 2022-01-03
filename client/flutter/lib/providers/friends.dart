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
}